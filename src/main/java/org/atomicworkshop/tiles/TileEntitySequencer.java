package org.atomicworkshop.tiles;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import org.atomicworkshop.sequencing.MusicPlayer;
import org.atomicworkshop.sequencing.Pattern;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.sequencing.SequencerSet;
import java.util.UUID;

public class TileEntitySequencer extends TileEntity
{

	private static UUID demoSongUUID = UUID.randomUUID();

	private Sequencer sequencer;

	@Override
	protected void setWorldCreate(World worldIn)
	{
		super.setWorldCreate(worldIn);
		createDemoSong();
	}

	@Override
	public void setWorld(World worldIn)
	{
		super.setWorld(worldIn);
		if (worldIn != null) {
			createDemoSong();
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
		sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.PIANO);
		sequencer.setAdjacentNoteBlock(EnumFacing.SOUTH, Instrument.BASSGUITAR);
		sequencer.setAdjacentNoteBlock(EnumFacing.EAST, Instrument.BELL);

		final Pattern demoPattern1 = new Pattern();
		demoPattern1.setPitchAtInternal(0, 6);
		demoPattern1.setPitchAtInternal(1, 10);
		demoPattern1.setPitchAtInternal(2, 13);

		demoPattern1.setPitchAtInternal(4, 8);
		demoPattern1.setPitchAtInternal(5, 11);
		demoPattern1.setPitchAtInternal(6, 15);

		demoPattern1.setPitchAtInternal(8, 10);
		demoPattern1.setPitchAtInternal(9, 13);
		demoPattern1.setPitchAtInternal(10, 17);

		demoPattern1.setPitchAtInternal(12, 11);
		demoPattern1.setPitchAtInternal(13, 15);
		demoPattern1.setPitchAtInternal(14, 18);

		sequencer.setPattern(0, demoPattern1);
		demoSong.updateBpm();

		MusicPlayer.playSong(demoSong);
	}



	private boolean hasSynchronizer()
	{
		return false;
	}

	@Override
	public void onChunkUnload()
	{
		stopPlaying();
	}

	public void stopPlaying()
	{
		MusicPlayer.stopPlaying(new SequencerSet(world, demoSongUUID));
	}
}
