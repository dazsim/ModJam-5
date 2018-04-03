package org.atomicworkshop.jammachine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.Reference.NBT;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.sequencing.SequencerSet;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityController extends TileEntity
{
    //FIXME: If breaking sequencer, write last state and drop it as item into the world.

    private static final int IS_PLAYING = 0;
    private static final int CHANGE_SECTION = 2;

    private boolean hasCard = false;

    private UUID sequencerSetId;
    private SequencerSet sequencerSet = null;
    private boolean isPlaying;

    private int displayedSection = 0;

    private Sequencer selectedSequencerA = null;
    private Sequencer selectedSequencerB = null;


    public TileEntityController()
    {
        sequencerSetId = UUID.randomUUID();
    }

    @Override
    public void onChunkUnload()
    {
        stopPlaying();
    }

    @Override
    protected void setWorldCreate(World worldIn)
    {
        setWorld(worldIn);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        JamMachineMod.logger.info("read from NBT");
        super.readFromNBT(compound);

        boolean wasPlaying = isPlaying;
        isPlaying = compound.getBoolean(NBT.isPlaying);
        hasCard = compound.getBoolean(NBT.hasCard);
        sequencerSetId = compound.getUniqueId(NBT.songId);
        if (sequencerSetId.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
            sequencerSetId = UUID.randomUUID();
        }

        if (sequencerSet == null) {
            sequencerSet = new SequencerSet(world, UUID.randomUUID());
        }

        final NBTTagCompound compoundTag = compound.getCompoundTag(NBT.sequence);
        if (!compoundTag.hasNoTags())
        {
            sequencerSet.readFromNBT(compoundTag);
            JamMachineMod.logger.info("compoundTag: {}", compoundTag);

        }

        updatePlayStatus(wasPlaying);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        JamMachineMod.logger.info("writing to NBT");

        super.writeToNBT(compound);

        compound.setBoolean(NBT.isPlaying, isPlaying);
        compound.setBoolean(NBT.hasCard, hasCard);
        compound.setUniqueId(NBT.songId, sequencerSetId);

        if (sequencerSet != null)
        {
            compound.setTag(NBT.sequence, sequencerSet.writeToNBT());
        }
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        final NBTTagCompound updateTag = getUpdateTag();
        return new SPacketUpdateTileEntity(pos, 0, updateTag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
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

        sequencerSet.updateBpm();

        boolean updateBlock = false;
        for (final EnumFacing horizontal : EnumFacing.HORIZONTALS)
        {
            for (final Sequencer sequencer : sequencerSet)
            {
                updateBlock |= sequencer.verifyNoteBlockFacing(horizontal);
            }
        }

        if (updateBlock) {
            sendUpdates();
        }

        MusicPlayer.playSong(sequencerSet);

        world.addBlockEvent(pos, getBlockType(), IS_PLAYING, 1);
    }

    private void sendUpdates() {
        if (world == null || pos == null) return;

        world.markBlockRangeForRenderUpdate(pos, pos);
        final IBlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 3);
        world.scheduleBlockUpdate(pos, getBlockType(), 0, 0);
        markDirty();
    }

    public void stopPlaying()
    {
        MusicPlayer.stopPlaying(new SequencerSet(world, sequencerSetId));
        world.addBlockEvent(pos, getBlockType(), IS_PLAYING, 0);

    }

    public void notifyPowered(boolean powered)
    {
        if (isPlaying != powered) {
            isPlaying = powered;
            world.addBlockEvent(pos, getBlockType(), IS_PLAYING, isPlaying ? 1 : 0);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type)
    {
        if (id == IS_PLAYING) {
            final boolean wasPlaying = isPlaying;
            isPlaying = type != 0;
            updatePlayStatus(wasPlaying);
            return true;
        }

        if (id == CHANGE_SECTION) {
            displayedSection = type;
            return true;
        }

        return false;
    }
}
