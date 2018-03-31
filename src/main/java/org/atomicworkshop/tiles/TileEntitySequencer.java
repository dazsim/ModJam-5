package org.atomicworkshop.tiles;

import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import org.atomicworkshop.sequencing.MusicPlayer;
import org.atomicworkshop.sequencing.Pattern;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.sequencing.SequencerSet;

public class TileEntitySequencer extends TileEntity
{
	@Override
	protected void setWorldCreate(World worldIn)
	{
		super.setWorldCreate(worldIn);
		final SequencerSet demoSong = new SequencerSet(world);

		final Sequencer sequencer = demoSong.addSequencer(pos);
		sequencer.setDesiredBPM(120);
		sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, Instrument.PIANO);

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

		demoPattern1.setPitchAtInternal(11, 11);
		demoPattern1.setPitchAtInternal(12, 15);
		demoPattern1.setPitchAtInternal(13, 18);

		sequencer.setPattern(0, demoPattern1);
		demoSong.updateBpm();

		MusicPlayer.playSong(demoSong);
	}
}
