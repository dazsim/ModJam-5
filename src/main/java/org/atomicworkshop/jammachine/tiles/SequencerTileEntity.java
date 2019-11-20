package org.atomicworkshop.jammachine.tiles;

import com.google.common.collect.ImmutableList;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.Reference.NBT;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.libraries.TileEntityTypeLibrary;
import org.atomicworkshop.jammachine.sequencing.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import org.atomicworkshop.jammachine.Reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class SequencerTileEntity extends TileEntity implements ITickableTileEntity, INameable
{

    private static final int IS_PLAYING = 0;
    private static final int CHANGE_PATTERN = 1;
    private static final int RUN_PROGRAM = 2;
    private static final int ENABLE_NOTE = 3;
    private static final int DISABLE_NOTE = 4;
    private static final int CHANGE_BPM = 5;
    private boolean hasCard = false;
    private String customName;

    public SequencerTileEntity()
    {
        super(TileEntityTypeLibrary.sequencer_tile);
        sequencerSetId = UUID.randomUUID();
    }

    private UUID sequencerSetId;
    //TODO: fix privacy later, right now the TESR needs access
    //FIXME: If breaking sequencer, write last state and drop it as item into the world.

    public Sequencer sequencer = null;
    private boolean isPlaying;

    private boolean hasController()
    {
        return false;
    }
    public boolean getHasCard()
    {
        return hasCard;
    }

    public void setHasCard(boolean iHasCard)
    {
        hasCard = iHasCard;
        sendUpdates();
    }

    @Override
    public void onChunkUnloaded()
    {
        stopPlaying();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (sequencer == null) {
            sequencer = new Sequencer(world, pos);
        } else {
            sequencer.setWorld(world);
        }
        MusicPlayer.startTracking(sequencer);
    }

    @Override
    public void setPos(BlockPos pos) {
        super.setPos(pos);

        if (sequencer == null) {
            sequencer = new Sequencer(world, this.pos);
        } else {
            sequencer.setPos(pos);
        }
        MusicPlayer.startTracking(sequencer);
    }

    @Override
    public void read(CompoundNBT compound)
    {
        //JamMachineMod.LOGGER.info("read from NBT");
        super.read(compound);

        final boolean wasPlaying = isPlaying;
        isPlaying = compound.getBoolean(NBT.isPlaying);
        hasCard = compound.getBoolean(NBT.hasCard);
        sequencerSetId = compound.getUniqueId(NBT.songId);

        if (sequencerSetId.equals(Reference.EMPTY_UUID)) {
            sequencerSetId = UUID.randomUUID();
        }

        if (sequencer == null) {
            sequencer = new Sequencer(world, pos);
        }

        readCustomDataFromNBT(compound);

        updatePlayStatus(wasPlaying);
    }

    private void readCustomDataFromNBT(CompoundNBT compound)
    {
        final CompoundNBT compoundTag = compound.getCompound(NBT.sequence);
        if (!compoundTag.isEmpty())
        {
            sequencer.readFromNBT(compoundTag);
            //JamMachineMod.LOGGER.info("compoundTag: {}", compoundTag);

            if (sequencer.getBeatsPerMinute() < 60) {
                sequencer.setDesiredBPM(120);
            }

            this.customName = compoundTag.getString(NBT.name);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound)
    {
        super.write(compound);

        compound.putBoolean(NBT.isPlaying, isPlaying);
        compound.putBoolean(NBT.hasCard, hasCard);
        compound.putUniqueId(NBT.songId, sequencerSetId);

        writeCustomDataToNBT(compound);

        //JamMachineMod.LOGGER.info("writing to NBT");
        return compound;
    }

    private CompoundNBT writeCustomDataToNBT(CompoundNBT compound)
    {
        if (hasCustomName()) {
            compound.putString(NBT.name, customName);
        }
        if (sequencer != null)
        {
            compound.put(NBT.sequence, sequencer.writeToNBT());
        }
        return compound;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        final CompoundNBT updateTag = getUpdateTag();
        return new SUpdateTileEntityPacket(pos, 0, updateTag);
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag()
    {
        return write(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt)
    {
        super.onDataPacket(net, pkt);
        final boolean wasPlaying = isPlaying;
        handleUpdateTag(pkt.getNbtCompound());

        updatePlayStatus(wasPlaying);
    }

    private void updatePlayStatus(boolean wasPlaying)
    {
        if (isPlaying && !wasPlaying) {
            startPlaying();
        } else if (!isPlaying && wasPlaying) {
            stopPlaying();
        }
    }

    private void startPlaying()
    {
        if (world == null || pos == null || !world.isRemote) return;

        //TODO: Find Synchronizer and get the Sequencer set for it.
        final SequencerSet sequencerSet;

        if (!hasController()) {
            sequencerSet = new SequencerSet(sequencerSetId);
        } else {
            //TODO: Resolve SequencerSet from sequencer
            sequencerSet = new SequencerSet(sequencerSetId);
        }

        sequencerSet.addSequencer(sequencer);

        sequencerSet.updateBpm();

        boolean updateBlock = false;
        for (final Direction horizontal : Reference.HORIZONTALS)
        {
            updateBlock |= sequencer.verifyNoteBlockFacing(horizontal);
        }
        if (updateBlock) {
            sendUpdates();
        }

        MusicPlayer.playSong(sequencerSet);

        world.addBlockEvent(pos, this.getBlockState().getBlock(), IS_PLAYING, 1);
    }

    private void sendUpdates() {
        if (world == null || pos == null) return;

        //world.markBlockRangeForRenderUpdate(pos, pos);
        final BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        //world.scheduleBlockUpdate(pos, this.getBlockState().getBlock(), 0, 0);
        markDirty();
    }

    public void stopPlaying()
    {
        if (sequencer != null)
        {
            sequencer.setCurrentInterval(-1);
        }

        MusicPlayer.stopPlaying(sequencerSetId);
        world.addBlockEvent(pos, this.getBlockState().getBlock(), IS_PLAYING, 0);

    }

    public void notifyPowered(boolean powered)
    {
        if (isPlaying != powered) {
            isPlaying = powered;
            world.addBlockEvent(pos, this.getBlockState().getBlock(), IS_PLAYING, isPlaying ? 1 : 0);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        final int controlCode = id & 7;

        if (controlCode == IS_PLAYING) {
            final boolean wasPlaying = isPlaying;
            isPlaying = type != 0;
            updatePlayStatus(wasPlaying);
            return true;
        }
        if (controlCode == CHANGE_PATTERN) {
            sequencer.setPendingPatternIndex(type);
            if (!isPlaying)
            {
                sequencer.setCurrentPatternIndex(type);
            }
            return true;
        }
        if (controlCode == RUN_PROGRAM) {
            sequencer.setProgramming(type != 0);
            return true;
        }
        if (controlCode == CHANGE_BPM) {
            sequencer.setDesiredBPM(type);
            return true;
        }
        if (controlCode == ENABLE_NOTE) {
            sequencer.getCurrentPattern().setPitchAtInternal(id >> 4, type);
            ImmutableList<AdjacentNoteBlock> availableNoteBlocks = sequencer.getAvailableNoteBlocks();
            if (!availableNoteBlocks.isEmpty())
            {
                AdjacentNoteBlock noteBlock = availableNoteBlocks.get(0);
                MusicPlayer.playNote(sequencer, noteBlock, (byte)type);
            }
            return true;
        }
        if (controlCode == DISABLE_NOTE) {
            sequencer.getCurrentPattern().resetPitchAtInterval(id >> 4, type);
            return true;
        }

        return false;
    }

    @Override
    public void tick()
    {
        if (sequencer == null) return;

        final int blockToCheck = (int)(world.getGameTime() & 3);
        final Direction facing = Direction.byHorizontalIndex(blockToCheck);

        sequencer.verifyNoteBlockFacing(facing);
    }

    public void loadFromCard(ItemStack heldItemStack)
    {
        if (heldItemStack.getTag()!=null)
        {
            readCustomDataFromNBT(heldItemStack.getTag());
        }
        if (!world.isRemote)
        {
            sendUpdates();
        }
    }

    public ItemStack saveToCard() {
        final ItemStack i = new ItemStack(ItemLibrary.punchCardWritten,1);
        i.setTag(writeCustomDataToNBT(new CompoundNBT()));

        return i;
    }

    public boolean checkPlayerInteraction(double x, double z, PlayerEntity playerIn)
    {
        if (sequencer == null) {
            sequencer = new Sequencer(world, pos);
            MusicPlayer.startTracking(sequencer);
        }

        if (x < 0 || x > 1 || z < 0 || z > 1) return false;

        if (checkSequenceGridInteraction(x, z)) return true;
        if (checkBankInteraction(x, z)) return true;
        if (checkBPMInteraction(x, z)) return true;
        if (checkRunButtonInteraction(x, z)) return true;
        if (checkCardSlotInteraction(x, z)) {
            ejectCard(playerIn.posX, playerIn.posY, playerIn.posZ);
            return true;
        }

        return false;
    }

    private boolean checkSequenceGridInteraction(double x, double z)
    {
        //Scale to button grid, these may not be exact because we're using item rendering instead of generating quads.
        //Minecraft probably applies additional scaling/transforms when rendering the item (they appear to be centered,
        //rather than top-left aligned as the code implies)
        x *= 28;
        x -= 2.5;
        z *= 30;
        z -= 2.4;
        JamMachineMod.LOGGER.info("adjusted hitlocation {},{}", x, z);

        JamMachineMod.LOGGER.info("checking player interaction at scaled {},{}", x, z);

        if (x >= 0 && x < 16 && z >= 0 && z < 25) {
            if (sequencer == null) {
                sequencer = new Sequencer(world, pos);
                MusicPlayer.startTracking(sequencer);
            }

            //Hit a sequence button
            final Pattern currentPattern = sequencer.getCurrentPattern();

            final int pitch = 24 - (int)z;
            final int interval = (int)x;

            final boolean isEnabled = currentPattern.invertPitchAtInternal(interval, pitch);


            JamMachineMod.LOGGER.info("Inverting pitch {} at interval {} - {}", pitch, interval, isEnabled);
            if (!world.isRemote)
            {
                if (isEnabled) {
                    world.addBlockEvent(pos, getBlockState().getBlock(), ENABLE_NOTE | (interval << 4), pitch);
                } else {
                    world.addBlockEvent(pos, getBlockState().getBlock(), DISABLE_NOTE | (interval << 4), pitch);
                }
                markDirty();
            }
            return true;
        }
        return false;
    }

    private boolean checkCardSlotInteraction(double clickX, double clickZ)
    {
        //check coords for ejecting a card.
        if (this.hasCard)
        {
            final double cardSlotLeft = 0.7192042839004351;
            final double cardSlotRight = 0.9325510942881259;
            final double cardSlotTop = 0.8448828111319173;
            final double cardSlotBottom = 0.9298803321497289;
            if ((clickX>= cardSlotLeft && clickX<= cardSlotRight) && (clickZ>= cardSlotTop && clickZ<= cardSlotBottom))
            {
                return true;
            }
        }
        return false;
    }

    private boolean checkRunButtonInteraction(double clickX, double clickZ)
    {
        //check coords for ejecting a card.
        final double runButtonLeft = 0.7192042839004351;
        final double runButtonRight = 0.9325510942881259;
        final double runButtonTop = 0.3479098384432291;
        final double runButtonBottom = 0.41390746154313085;
        if ((clickX>= runButtonLeft && clickX<= runButtonRight) && (clickZ>= runButtonTop && clickZ<= runButtonBottom))
        {
            if (!world.isRemote)
            {
                sequencer.setProgramming(!sequencer.isProgramming());

                world.addBlockEvent(pos, getBlockState().getBlock(), RUN_PROGRAM, sequencer.isProgramming() ? 1 : 0);
            }
            return true;
        }

        return false;
    }

    private boolean checkBankInteraction(double clickX, double clickZ)
    {
        //check coords for setting bank
        final double bankLeft = 0.707334934013371;
        final double bankRight = 0.9897283611457244;
        final double bankTop = 0.4415902484574872;
        final double bankBottom = 0.5620629764642562;

        if ((clickX>=bankLeft && clickX<=bankRight) && (clickZ>=bankTop && clickZ<=bankBottom))
        {
            if (sequencer == null) {
                sequencer = new Sequencer(world, pos);
                MusicPlayer.startTracking(sequencer);
            }
            final double bankWidth = bankRight - bankLeft;
            final double bankHeight = bankBottom-bankTop;
            final double offX = clickX - bankLeft;
            final double offZ = clickZ - bankTop;
            int index;

            index = (int)((offX / bankWidth) * 4);
            index += ((int)((offZ / bankHeight) * 2)) * 4;


			/*sequencer.setPendingPatternIndex(index);
			if (!isPlaying)
			{
				sequencer.setCurrentPatternIndex(index);
			}*/

            if (!world.isRemote)
            {
                world.addBlockEvent(pos, getBlockState().getBlock(), CHANGE_PATTERN, index);
                markDirty();
            }
            return true;
        }
        return false;
    }

    private boolean checkBPMInteraction(double clickX, double clickZ)
    {
        //BPM UI buttons
        final double bpmLeft = 0.7045204265288701;
        final double bpmRight = 0.9327918869342732;
        final double bpmTop = 0.21970650094097977;
        final double bpmBottom = 0.2705630830397361;

        if ((clickX>= bpmLeft && clickX<= bpmRight) && (clickZ>= bpmTop && clickZ<= bpmBottom))
        {
            final double bpmUIWidth = bpmRight - bpmLeft;
            final double offX = clickX - bpmLeft;

            final int index = (int)((offX / bpmUIWidth) * 4);

            switch (index) {
                case 0:
                    sequencer.setDesiredBPM(Math.max(sequencer.getBeatsPerMinute() - 10, 60));
                    break;
                case 1:
                    sequencer.setDesiredBPM(Math.max(sequencer.getBeatsPerMinute() - 1, 60));
                    break;
                case 2:
                    sequencer.setDesiredBPM(Math.min(sequencer.getBeatsPerMinute() +1, 240));
                    break;
                case 3:
                    sequencer.setDesiredBPM(Math.min(sequencer.getBeatsPerMinute() +10, 240));
                    break;
            }
            if (!world.isRemote)
            {
                world.addBlockEvent(pos, getBlockState().getBlock(), CHANGE_BPM, sequencer.getBeatsPerMinute());
                markDirty();
            }
            return true;
        }
        return false;
    }

    public void ejectCard(double x, double y, double z)
    {
        JamMachineMod.LOGGER.info("Eject Card");

        ItemStack card = saveToCard();
        if (!world.isRemote)
        {
            ItemEntity entityitem = new ItemEntity(this.world, x, y + 0.5, z, card);
            entityitem.setDefaultPickupDelay();
            world.addEntity(entityitem);
        }
        setHasCard(false);
    }

    public Sequencer getSequencer()
    {
        return sequencer;
    }


    /**
     * Returns true if this thing is named
     */
    public boolean hasCustomName()
    {
        return this.customName != null && !this.customName.isEmpty();
    }

    public void setCustomName(String name)
    {
        this.customName = name;
        sendUpdates();
    }

    /**
     * Get the formatted ChatComponent that will be used for the sender's username in chat
     */
    public ITextComponent getDisplayName()
    {
        if (this.customName != null) {
            return new StringTextComponent(this.customName);
        }
        return null;
    }

    /**
     * Get the name of this object. For players this returns their username
     * @return
     */
    public ITextComponent getName()
    {
        return new TranslationTextComponent(Reference.Blocks.SEQUENCER.getPath());
    }
}