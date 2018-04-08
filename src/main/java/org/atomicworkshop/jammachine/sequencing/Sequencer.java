package org.atomicworkshop.jammachine.sequencing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.Reference.NBT;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static net.minecraftforge.event.world.NoteBlockEvent.Instrument.*;

public class Sequencer
{
	private final World world;
	private final BlockPos blockPos;
	private final Pattern[] patterns;
	private final AdjacentNoteBlock[] adjacentNoteBlocks;
	private UUID id;
	private ImmutableList<AdjacentNoteBlock> currentAdjacentNoteBlocks = ImmutableList.of();
	private int beatsPerMinute;
	private int pendingPatternIndex;
	private int currentPatternIndex;
	private int currentInterval = -1;
	private int noteBlockSearch;
	private String name;
	private boolean isProgramming;
	private LinkedList<Integer> programList = new LinkedList<>();
	private int programIndex;

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
		for (int i = 0; i < 8; i++)
		{
			patterns[i] = new Pattern();
		}

		beatsPerMinute = 120;

		id = UUID.randomUUID();
	}

	private void setAdjacentNoteBlock(EnumFacing direction, Instrument sound)
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

	private Instrument getInstrumentFromNoteBlock(EnumFacing direction) {
		return adjacentNoteBlocks[direction.getHorizontalIndex()].getInstrument();
	}

	BlockPos getBlockPos()
	{
		return blockPos;
	}

	public UUID getId() {
		return id;
	}

	public void setDesiredBPM(int beatsPerMinute)
	{
		this.beatsPerMinute = beatsPerMinute;
	}

	public int getBeatsPerMinute()
	{
		return beatsPerMinute;
	}

	public int getPendingPatternIndex()
	{
		return pendingPatternIndex < 0 ? 0 : pendingPatternIndex;
	}

	public void setPendingPatternIndex(int pendingPatternIndex)
	{
		if (pendingPatternIndex > patterns.length) {
			pendingPatternIndex %= patterns.length;
		}
		if (isProgramming) {
			programList.add(pendingPatternIndex);
		} else
		{
			this.pendingPatternIndex = pendingPatternIndex;
		}
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
		return currentPatternIndex < 0 ? 0 : currentPatternIndex;
	}

	public void updatePendingPattern()
	{
		if (isProgramming) {
			programIndex++;
			if (programIndex >= programList.size()) {
				programIndex = 0;
			}
			currentPatternIndex = programList.get(programIndex);

			int nextProgramIndex = programIndex + 1;
			if (nextProgramIndex >= programList.size()) {
				nextProgramIndex = 0;
			}

			pendingPatternIndex = programList.get(nextProgramIndex);

		} else
		{
			currentPatternIndex = pendingPatternIndex;
		}
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

	public void readFromNBT(NBTTagCompound compound)
	{
		id = compound.getUniqueId(NBT.sequencerId);
		if (id.equals(Reference.EMPTY_UUID)) {
			id = UUID.randomUUID();
		}
		beatsPerMinute = compound.getInteger(NBT.beatsPerMinute);

		currentPatternIndex = compound.getInteger(NBT.currentPatternIndex);
		pendingPatternIndex = compound.getInteger(NBT.pendingPatternIndex);
		name = compound.getString(NBT.name);

		final NBTTagList nbtPatterns = compound.getTagList(NBT.pattern, Constants.NBT.TAG_COMPOUND);

		for (int patternIndex = 0; patternIndex < patterns.length; patternIndex++)
		{
			final NBTTagCompound patternNBT = nbtPatterns.getCompoundTagAt(patternIndex);
			final Pattern pattern = patterns[patternIndex];

			for (int interval = 0; interval < 16; interval++)
			{
				final byte[] pitchesAtInterval = patternNBT.getByteArray(String.valueOf(interval));

				for (int i = 0; i < 25; i++)
				{
					pattern.resetPitchAtInterval(interval, i);
				}

				for (final byte pitch : pitchesAtInterval)
				{

					pattern.setPitchAtInternal(interval, pitch);
				}
			}
		}

		programList.clear();
		for (final int programPatternIndex : compound.getIntArray(NBT.program))
		{
			programList.add(programPatternIndex);
		}
		isProgramming = compound.getBoolean(NBT.isProgramming);
	}

	public NBTTagCompound writeToNBT()
	{
		final NBTTagCompound tagCompound = new NBTTagCompound();
		tagCompound.setUniqueId(NBT.sequencerId, id);
		tagCompound.setInteger(NBT.beatsPerMinute, beatsPerMinute);
		tagCompound.setInteger(NBT.currentPatternIndex, currentPatternIndex);
		tagCompound.setInteger(NBT.pendingPatternIndex, pendingPatternIndex);
		if (name != null)
		{
			tagCompound.setString(NBT.name, name);
		}

		final NBTTagList patternList = new NBTTagList();
		for (int patternIndex = 0; patternIndex < patterns.length; patternIndex++)
		{
			final Pattern pattern = patterns[patternIndex];

			final NBTTagCompound patternNBT = new NBTTagCompound();

			for (int interval = 0; interval < 16; interval++)
			{
				final Iterable<Byte> pitchesAtInterval = pattern.getPitchesAtInterval(interval);
				final ArrayList<Byte> pitchList = Lists.newArrayList(pitchesAtInterval);
				if (!pitchList.isEmpty()) {
					final NBTTagByteArray pitchArray = new NBTTagByteArray(pitchList);
					patternNBT.setTag(String.valueOf(interval), pitchArray);
				}
			}

			patternList.appendTag(patternNBT);
		}
		tagCompound.setTag(NBT.pattern, patternList);


		if (!programList.isEmpty())
		{
			final int[] programPatternIndices = new int[programList.size()];
			int index = 0;
			for (final Integer programPatternIndex : programList)
			{
				programPatternIndices[index++] = programPatternIndex;
			}

			tagCompound.setIntArray(NBT.program, programPatternIndices);
		}

		tagCompound.setBoolean(NBT.isProgramming, isProgramming);

		return tagCompound;
	}

	public int getCurrentInterval()
	{
		return currentInterval;
	}

	public void setCurrentInterval(int currentInterval)
	{
		this.currentInterval = currentInterval;
	}

	public int incrementNoteBlockNumber()
	{
		++noteBlockSearch;
		while (noteBlockSearch >= currentAdjacentNoteBlocks.size()) {
			noteBlockSearch -= currentAdjacentNoteBlocks.size();
		}
		return noteBlockSearch;
	}

	public boolean incrementInterval()
	{
		boolean marchControllerPattern = false;
		++currentInterval;
		if (currentInterval >= 16) {
			currentInterval = 0;
			//Setting the noteBlockSearch to 0 here ensures that the sounds played per pattern are deterministic
			//for a given pattern
			noteBlockSearch = 0;

			marchControllerPattern = true;
		}

		if (currentInterval == 0) {

			updatePendingPattern();
		}

		return marchControllerPattern;
	}

	public boolean verifyNoteBlockFacing(EnumFacing facing)
	{
		final BlockPos offset = blockPos.offset(facing.getOpposite());
		final IBlockState noteBlockState = world.getBlockState(offset);

		@Nullable
		final Instrument instrument;
		if (Blocks.NOTEBLOCK.equals(noteBlockState.getBlock()))
		{
			final IBlockState instrumentBlockState = world.getBlockState(offset.down());
			instrument = getInstrumentFromBlockState(instrumentBlockState);
		} else
		{
			instrument = null;
		}

		final Instrument instrumentFromNoteBlock = getInstrumentFromNoteBlock(facing);

		if (instrument != instrumentFromNoteBlock) {
			setAdjacentNoteBlock(facing, instrument);
			return true;
		}
		return false;
	}

	@SuppressWarnings("ObjectEquality") //Disabled because this is super close to vanilla's TileEntity stuff.
	private static Instrument getInstrumentFromBlockState(IBlockState state)
	{
		//Blatantly ripped from TileEntityNote
		final Material material = state.getMaterial();

		if (material == Material.ROCK)
		{
			return BASSDRUM;
		}

		if (material == Material.SAND)
		{
			return SNARE;
		}

		if (material == Material.GLASS)
		{
			return CLICKS;
		}

		if (material == Material.WOOD)
		{
			return BASSGUITAR;
		}

		final Block block = state.getBlock();

		if (block == Blocks.CLAY)
		{
			return FLUTE;
		}

		if (block == Blocks.GOLD_BLOCK)
		{
			return BELL;
		}

		if (block == Blocks.WOOL)
		{
			return GUITAR;
		}

		if (block == Blocks.PACKED_ICE)
		{
			return CHIME;
		}

		if (block == Blocks.BONE_BLOCK)
		{
			return XYLOPHONE;
		}
		return PIANO;

	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public void setProgramming(boolean isProgramming)
	{
		this.isProgramming = isProgramming;
		programList.clear();
		programList.add(currentPatternIndex < 0 ? 0 : currentPatternIndex);
	}

	public boolean isProgramming()
	{
		return isProgramming;
	}

	public void reset()
	{
		this.programIndex = 0;
		setCurrentInterval(-1);
		setCurrentPatternIndex(getPendingPatternIndex());
	}

	public int getProgramLength()
	{
		return programList.size();
	}
}
