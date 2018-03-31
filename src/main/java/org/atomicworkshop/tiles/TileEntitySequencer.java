package org.atomicworkshop.tiles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import org.atomicworkshop.Reference.NBT;
import org.atomicworkshop.sequencing.MusicPlayer;
import org.atomicworkshop.sequencing.Pattern;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.sequencing.SequencerSet;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntitySequencer extends TileEntity
{

	public TileEntitySequencer()
	{
		demoSongUUID = UUID.randomUUID();
	}

	private UUID demoSongUUID;

	private Sequencer sequencer;
	private boolean isPlaying;

	private boolean hasSynchronizer()
	{
		return false;
	}

	@Override
	public void onChunkUnload()
	{
		stopPlaying();
	}

	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		isPlaying = compound.getBoolean(NBT.isPlaying);

		demoSongUUID = compound.getUniqueId(NBT.songId);
		if (demoSongUUID.equals(UUID.fromString("00000000-0000-0000-0000-000000000000"))) {
			demoSongUUID = UUID.randomUUID();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);

		compound.setBoolean(NBT.isPlaying, isPlaying);
		compound.setUniqueId(NBT.songId, demoSongUUID);

		return compound;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound updateTag = getUpdateTag();
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
		boolean wasPlaying = isPlaying;
		handleUpdateTag(pkt.getNbtCompound());

		if (isPlaying && !wasPlaying) {
			createDemoSong();

		} else if (!isPlaying && wasPlaying) {
			MusicPlayer.stopPlaying(new SequencerSet(world, demoSongUUID));
			sendUpdates();
		}
	}

	private void sendUpdates() {
		world.markBlockRangeForRenderUpdate(pos, pos);
		IBlockState state = world.getBlockState(pos);
		world.notifyBlockUpdate(pos, state, state, 3);
		world.scheduleBlockUpdate(pos, getBlockType(),0,0);
		markDirty();
	}

	public void stopPlaying()
	{
		MusicPlayer.stopPlaying(new SequencerSet(world, demoSongUUID));
	}

	public void notifyPowered(boolean powered)
	{
		if (isPlaying != powered) {
			isPlaying = powered;
			sendUpdates();
		}
	}


	private void createDemoSong()
	{
		if (world == null) return;
		//TODO: Find Synchronizer and get the Sequencer set for it.
		final SequencerSet demoSong;

		if (!hasSynchronizer()) {
			demoSong = new SequencerSet(world, demoSongUUID);
		} else {
			//TODO: Resolve SequencerSet from sequencer
			demoSong = new SequencerSet(world, demoSongUUID);
		}

		sequencer = demoSong.addSequencer(pos);
		sequencer.setDesiredBPM(120);

		int pattern = world.rand.nextInt(4);
		final Pattern demoPattern = new Pattern();
		if (pattern == 0)
		{
			sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.PIANO);
			sequencer.setAdjacentNoteBlock(EnumFacing.SOUTH, Instrument.BASSGUITAR);
			sequencer.setAdjacentNoteBlock(EnumFacing.EAST, Instrument.PIANO);

			demoPattern.setPitchAtInternal(0, 6);
			demoPattern.setPitchAtInternal(1, 10);
			demoPattern.setPitchAtInternal(2, 13);

			demoPattern.setPitchAtInternal(4, 8);
			demoPattern.setPitchAtInternal(5, 11);
			demoPattern.setPitchAtInternal(6, 15);

			demoPattern.setPitchAtInternal(8, 10);
			demoPattern.setPitchAtInternal(9, 13);
			demoPattern.setPitchAtInternal(10, 17);

			demoPattern.setPitchAtInternal(12, 11);
			demoPattern.setPitchAtInternal(13, 15);
			demoPattern.setPitchAtInternal(14, 18);

		} else if (pattern == 1) {
			sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.BASSDRUM);

			demoPattern.setPitchAtInternal(0, 6);
			demoPattern.setPitchAtInternal(8, 6);

		} else if (pattern == 2) {
			sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.SNARE);

			demoPattern.setPitchAtInternal(4, 6);
			demoPattern.setPitchAtInternal(12, 6);
		} else if (pattern == 3) {
			sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.CLICKS);

			demoPattern.setPitchAtInternal(1, 14);
			demoPattern.setPitchAtInternal(2, 18);
			demoPattern.setPitchAtInternal(3, 5);

			demoPattern.setPitchAtInternal(5, 14);
			demoPattern.setPitchAtInternal(6, 18);
			demoPattern.setPitchAtInternal(7, 5);

			demoPattern.setPitchAtInternal(9, 16);
			demoPattern.setPitchAtInternal(10, 18);
			demoPattern.setPitchAtInternal(11, 5);

			demoPattern.setPitchAtInternal(13, 14);
			demoPattern.setPitchAtInternal(14, 18);
			demoPattern.setPitchAtInternal(15, 5 );
		}

		sequencer.setPattern(0, demoPattern);

		demoSong.updateBpm();

		MusicPlayer.playSong(demoSong);
	}
}
