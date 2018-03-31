package org.atomicworkshop.tiles;

import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.atomicworkshop.sequencing.MusicPlayer;

public class TileEntitySequencer extends TileEntity
{
	public void update()
	{
		final MusicPlayer.SequencerSet demoSong = new MusicPlayer.SequencerSet();

		MusicPlayer.Sequencer sequencer = demoSong.addSequencer(pos);
		sequencer.setAdjacentNoteBlock(EnumFacing.NORTH, SoundEvents.BLOCK_NOTE_HARP);
	}
}
