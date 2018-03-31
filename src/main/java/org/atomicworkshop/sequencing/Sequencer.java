package org.atomicworkshop.sequencing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import java.util.List;
import java.util.UUID;

public class Sequencer
{
	private final World world;
	private final BlockPos blockPos;
	private Pattern[] patterns;
	private AdjacentNoteBlock[] adjacentNoteBlocks;
	private ImmutableList<AdjacentNoteBlock> currentAdjacentNoteBlocks = ImmutableList.of();
	private int beatsPerMinute;
	private int pendingPatternIndex;
	private int currentPatternIndex;

	public Sequencer(World world, BlockPos blockPos)
	{
		this.world = world;
		this.blockPos = blockPos;
		adjacentNoteBlocks = new AdjacentNoteBlock[] {
				new AdjacentNoteBlock(EnumFacing.NORTH),
				new AdjacentNoteBlock(EnumFacing.EAST),
				new AdjacentNoteBlock(EnumFacing.SOUTH),
				new AdjacentNoteBlock(EnumFacing.WEST),
		};

		patterns = new Pattern[8];
	}

	public void setAdjacentNoteBlock(EnumFacing direction, Instrument sound)
	{
		adjacentNoteBlocks[direction.getHorizontalIndex()].setInstrument(sound);
		final List<AdjacentNoteBlock> availableNoteBlocks = Lists.newArrayList();
		for (final AdjacentNoteBlock adjacentNoteBlock : adjacentNoteBlocks)
		{
			if (adjacentNoteBlock.canPlay())
			{
				availableNoteBlocks.add(adjacentNoteBlock);
			}
		}
		currentAdjacentNoteBlocks = ImmutableList.copyOf(availableNoteBlocks);
	}

	public Instrument getInstrumentFromNoteBlock(EnumFacing direction) {
		return adjacentNoteBlocks[direction.getHorizontalIndex()].getInstrument();
	}

	public BlockPos getBlockPos()
	{
		return blockPos;
	}

	public void setDesiredBPM(int beatsPerMinute)
	{
		this.beatsPerMinute = beatsPerMinute;
	}

	public void setPattern(int patternIndex, Pattern pattern)
	{
		if (patternIndex > patterns.length) {
			patternIndex %= patterns.length;
		}
		patterns[patternIndex] = pattern;
	}

	public int getBeatsPerMinute()
	{
		return beatsPerMinute;
	}

	public int getPendingPatternIndex()
	{
		return pendingPatternIndex;
	}

	public void setPendingPatternIndex(int pendingPatternIndex)
	{
		if (pendingPatternIndex > patterns.length) {
			pendingPatternIndex %= patterns.length;
		}
		this.pendingPatternIndex = pendingPatternIndex;
	}

	public void setCurrentPatternIndex(int currentPatternIndex)
	{
		if (currentPatternIndex > patterns.length) {
			currentPatternIndex %= patterns.length;
		}
		this.currentPatternIndex = currentPatternIndex;
	}

	public int getCurrentPatternIndex()
	{
		return currentPatternIndex;
	}

	public void updatePendingPattern()
	{
		currentPatternIndex = pendingPatternIndex;
	}

	public Pattern getCurrentPattern()
	{
		return patterns[currentPatternIndex];
	}

	public ImmutableList<AdjacentNoteBlock> getAvailableNoteBlocks()
	{
		return currentAdjacentNoteBlocks;
	}

	public World getWorld()
	{
		return world;
	}
}
