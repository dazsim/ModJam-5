package org.atomicworkshop.jammachine.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.Reference.NBT;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.sequencing.Pattern;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.sequencing.SequencerSet;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntitySequencer extends TileEntity implements ITickable
{

	private static final int IS_PLAYING = 0;
	private static final int CHANGE_PATTERN = 1;
	private boolean hasCard = false;
	
	public TileEntitySequencer()
	{
		sequencerSetId = UUID.randomUUID();
	}

	private UUID sequencerSetId;
	//TODO: fix privacy later, right now the TESR needs access
	//FIXME: If breaking sequencer, write last state and drop it as item into the world.

	private Sequencer sequencer = null;
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

		final boolean wasPlaying = isPlaying;
		isPlaying = compound.getBoolean(NBT.isPlaying);
		hasCard = compound.getBoolean(NBT.hasCard);
		sequencerSetId = compound.getUniqueId(NBT.songId);

		if (sequencerSetId.equals(Reference.EMPTY_UUID)) {
			sequencerSetId = UUID.randomUUID();
		}

		readCustomDataFromNBT(compound);

		updatePlayStatus(wasPlaying);
	}

	private void readCustomDataFromNBT(NBTTagCompound compound)
	{
		final NBTTagCompound compoundTag = compound.getCompoundTag(NBT.sequence);
		if (!compoundTag.hasNoTags())
		{
			if (sequencer == null) {
				sequencer = new Sequencer(world, pos);
				MusicPlayer.startTracking(sequencer);
			}

			sequencer.readFromNBT(compoundTag);
			JamMachineMod.logger.info("compoundTag: {}", compoundTag);

			if (sequencer.getBeatsPerMinute() < 60) {
				sequencer.setDesiredBPM(120);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		compound.setBoolean(NBT.isPlaying, isPlaying);
		compound.setBoolean(NBT.hasCard, hasCard);
		compound.setUniqueId(NBT.songId, sequencerSetId);

		writeCustomDataToNBT(compound);

		JamMachineMod.logger.info("writing to NBT");
		return compound;
	}

	private NBTTagCompound writeCustomDataToNBT(NBTTagCompound compound)
	{
		if (sequencer != null)
		{
			compound.setTag(NBT.sequence, sequencer.writeToNBT());
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
		for (final EnumFacing horizontal : EnumFacing.HORIZONTALS)
		{
			updateBlock |= sequencer.verifyNoteBlockFacing(horizontal);
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
		if (sequencer != null)
		{
			sequencer.setCurrentInterval(-1);
		}

		MusicPlayer.stopPlaying(sequencerSetId);
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
		if (id == CHANGE_PATTERN) {
			sequencer.setPendingPatternIndex(type);
			if (!isPlaying)
			{
				sequencer.setCurrentPatternIndex(type);
			}
			return true;
		}
		
		return false;
	}

	@Override
	public void update()
	{
		if (sequencer == null) return;

		final int blockToCheck = (int)(world.getTotalWorldTime() & 3);
		final EnumFacing facing = EnumFacing.getHorizontal(blockToCheck);

		sequencer.verifyNoteBlockFacing(facing);
	}

	public void loadFromCard(ItemStack heldItemStack)
	{
		if (heldItemStack.getTagCompound()!=null)
		{
			readCustomDataFromNBT(heldItemStack.getTagCompound());
		}
		if (!world.isRemote)
		{
			sendUpdates();
		}
	}

	public ItemStack saveToCard() {
		ItemStack i = new ItemStack(ItemLibrary.punchCardWritten,1);
		i.setTagCompound(writeCustomDataToNBT(new NBTTagCompound()));
		
		return i;
	}

	public boolean checkPlayerInteraction(double x, double z,EntityPlayer playerIn)
	{
		//if (world.isRemote) return false;
		//back up input coordinates
		double backX,backZ;
		
		backX = x;
		backZ = z;

		//x = 1-x;
		//z = 1-z;
		if (x < 0 || x > 1 || z < 0 || z > 1) return false;

		JamMachineMod.logger.info("adjusted hitlocation {},{}", x, z);
		//Scale to button grid, these may not be exact because we're using item rendering instead of generating quads.
		//Minecraft probably applies additional scaling/transforms when rendering the item (they appear to be centered,
		//rather than top-left aligned as the code implies)
		x *= 28; x -= 2.5;
		z *= 29; z -= 0.5;

		JamMachineMod.logger.info("checking player interaction at scaled {},{}", x, z);

		if (x >= 0 && x < 16 && z >= 0 && z < 26) {
			if (sequencer == null) {
				sequencer = new Sequencer(world, pos);
				MusicPlayer.startTracking(sequencer);
			}

			//Hit a sequence button
			final Pattern currentPattern = sequencer.getCurrentPattern();

			final int pitch = 25 - (int)z;
			final int interval = (int)x;

			boolean isEnabled = currentPattern.invertPitchAtInternal(interval, pitch);

			JamMachineMod.logger.info("Inverting pitch {} at interval {} - {}", pitch, interval, isEnabled);
			if (!world.isRemote)
			{
				sendUpdates();
			}
			return true;
		}
		//did not click any buttons on pattern array
		//check coords for ejecting a card.
		if (this.hasCard)
		{
			if ((backX>=0.7192042839004351 && backX<=0.9325510942881259) && (backZ>=0.8448828111319173 && backZ<=0.9298803321497289))
			{
				ejectCard(playerIn.posX, playerIn.posY, playerIn.posZ);
				return true;
			}
		}
		
		//check coords for setting bank
		//0.7062424864522878,0.43397610552808197
		//0.9593446274414177,0.5463901310950732
		if ((backX>=0.7062424864522878 && backX<=0.9593446274414177) && (backZ>=0.43397610552808197 && backZ<=0.5463901310950732))
		{
			double bankWidth = 0.9593446274414177 - 0.7062424864522878;
			double bankHeight = 0.5463901310950732 - 0.43397610552808197;
			double offX = backX - 0.7062424864522878;
			double offZ = backZ - 0.43397610552808197;
			int index = 0;
			if (offX < (bankWidth/4))
			{
				if (offZ < (bankHeight/2))
				{
					//set to 0
					index = 0;
					
				} else
				{
					//set to 4
					index = 4;
					
				}
			} else
			if (offX < (bankWidth/2))
			{
				if (offZ < (bankHeight/2))
				{
					//set to 1
					index = 1;
				} else
				{
					//set to 5
					index = 5;
				}
			} else
			if (offX < ((bankWidth*3)/4))
			{
				if (offZ < (bankHeight/2))
				{
					//set to 2
					index = 2;
				} else
				{
					//set to 6
					index=6;
				}
			} else
			if (offZ < (bankHeight/2))
			{
				//set to 3
				index=3;
			} else
			{
				//set to 7
				index=7;
			}
			world.addBlockEvent(pos, getBlockType(), CHANGE_PATTERN, index);
			sequencer.setPendingPatternIndex(index);
			if (!isPlaying)
			{
				sequencer.setCurrentPatternIndex(index);
			}

			return true;
		}
		//0.7045204265288701,0.21970650094097977
		//0.9327918869342732,0.2705630830397361
		//BPM UI buttons
		if ((backX>=0.7045204265288701 && backX<=0.9327918869342732) && (backZ>=0.21970650094097977 && backZ<=0.2705630830397361))
		{
			double bpmUIWidth = 0.9327918869342732 - 0.7045204265288701;
			double offX = backX - 0.7045204265288701;
			if (offX<=(bpmUIWidth/4))
			{
				//-10
				if (sequencer.getBeatsPerMinute()>10)
				{
					sequencer.setDesiredBPM(sequencer.getBeatsPerMinute()-10);
					if (!world.isRemote)
					{
						sendUpdates();
					}
					return true;
				}
				
			} else
			if (offX<=(bpmUIWidth/2))
			{
				//-1
				if (sequencer.getBeatsPerMinute()>1)
				{
					sequencer.setDesiredBPM(sequencer.getBeatsPerMinute()-1);
					if (!world.isRemote)
					{
						sendUpdates();
					}
					return true;
				}
			} else
			if (offX<=(bpmUIWidth*3)/4)
			{
				//+1
				if (sequencer.getBeatsPerMinute()<240)
				{
					sequencer.setDesiredBPM(sequencer.getBeatsPerMinute()+1);
					if (!world.isRemote)
					{
						sendUpdates();
					}
					return true;
				}
			} else
			{
				//+10
				if (sequencer.getBeatsPerMinute()<230)
				{
					sequencer.setDesiredBPM(sequencer.getBeatsPerMinute()+10);
					if (!world.isRemote)
					{
						sendUpdates();
					}
					return true;
				}
			}
		}
		return false;
	}

	public void ejectCard(double x, double y, double z)
	{
		JamMachineMod.logger.info("Eject Card");
		ItemStack card = saveToCard();

		if (!world.isRemote)
		{
			EntityItem entityitem = new EntityItem(this.world, x, y + 0.5, z, card);
			entityitem.setDefaultPickupDelay();
			world.spawnEntity(entityitem);
		}
		setHasCard(false);
	}

	public Sequencer getSequencer()
	{
		if (sequencer == null && world != null && pos != null) {
			sequencer = new Sequencer(world, pos);
		}

		return sequencer;
	}
}
