package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public class MusicPlayer
{
	public MusicPlayer()
	{

	}

	public static class SequencerSet {
		int beatsPerMinute;
		List<Sequencer> sequencers = Lists.newArrayList();

		public Sequencer addSequencer(BlockPos pos)
		{
			Sequencer newSequencer = new Sequencer(pos);
			sequencers.add(newSequencer);
			return newSequencer;
		}
	}

	public static class Sequencer
	{
		private final BlockPos blockPos;
		Pattern[] patterns;
		AdjacentNoteBlock[] adjacentNoteBlocks;

		public Sequencer(BlockPos blockPos)
		{
			this.blockPos = blockPos;
			adjacentNoteBlocks = new AdjacentNoteBlock[] {
					new AdjacentNoteBlock(EnumFacing.NORTH),
					new AdjacentNoteBlock(EnumFacing.EAST),
					new AdjacentNoteBlock(EnumFacing.SOUTH),
					new AdjacentNoteBlock(EnumFacing.WEST),
			};

			patterns = new Pattern[8];
		}

		public void setAdjacentNoteBlock(EnumFacing direction, SoundEvent sound)
		{
			adjacentNoteBlocks[direction.getHorizontalIndex()].sound = sound;
		}


		public BlockPos getBlockPos()
		{
			return blockPos;
		}
	}

	public static class Pattern {
		byte[][] patternData = new byte[16][24];
	}

	public static class AdjacentNoteBlock
	{
		public SoundEvent sound;
		EnumFacing direction;

		public AdjacentNoteBlock(EnumFacing direction)
		{
			this.direction = direction;
			sound = null;
		}
	}
}

