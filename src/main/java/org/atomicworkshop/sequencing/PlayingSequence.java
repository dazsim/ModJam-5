package org.atomicworkshop.sequencing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent.Play;
import java.util.List;

class PlayingSequence
{
	private final SequencerSet sequencerSet;
	private long nextTickTime;
	private int interval = -1;

	private int noteBlockSearch;


	PlayingSequence(SequencerSet sequencerSet)
	{
		this.sequencerSet = sequencerSet;
	}

	long getBeatsPerMinute()
	{
		return sequencerSet.getBeatsPerMinute();
	}

	long getNextTickTime()
	{
		return nextTickTime;
	}

	void setNextTickTime(long tickTime)
	{
		this.nextTickTime = tickTime;
	}

	void playNextInterval()
	{
		++interval;
		if (interval >= 16) {
			interval = 0;
			//Setting the noteBlockSearch to 0 here ensures that the sounds played per pattern are deterministic
			//for a given pattern
			noteBlockSearch = 0;
		}

		for (final Sequencer sequencer : sequencerSet)
		{
			if ((interval & 3) == 0) {
				sequencer.updatePendingPattern();
			}
			final BlockPos sequencerBlockPos = sequencer.getBlockPos();

			final ImmutableList<AdjacentNoteBlock> availableNoteBlocks = sequencer.getAvailableNoteBlocks();
			if (availableNoteBlocks.isEmpty()) continue;

			final Pattern currentPattern = sequencer.getCurrentPattern();
			for (final Byte pitchToPlay : currentPattern.getPitchesAtInterval(interval))
			{
				//Identify Note block to "play"

				++noteBlockSearch;
				if (noteBlockSearch >= availableNoteBlocks.size()) {
					noteBlockSearch %= availableNoteBlocks.size();
				}
				AdjacentNoteBlock noteBlock = availableNoteBlocks.get(noteBlockSearch);

				final BlockPos pos = sequencerBlockPos.offset(noteBlock.getDirection());
				final int instrumentId = noteBlock.getInstrument().ordinal();

				//Schedule sound to be played
				final World world = sequencerSet.getWorld();
				final Play e = new Play(world, pos, world.getBlockState(pos), pitchToPlay, instrumentId);
				if (MinecraftForge.EVENT_BUS.post(e)) continue;

				final int playingInstrumentId = e.getInstrument().ordinal();
				final int playingNoteId = e.getVanillaNoteId();
				final float pitch = (float)Math.pow(2.0D, (playingNoteId - 12) / 12.0D);
				SoundEvent instrument = getInstrument(playingInstrumentId);

				double x = pos.getX() + 0.5;
				double y = pos.getY() + 0.5;
				double z = pos.getZ() + 0.5;
				world.playSound(x, y, z, instrument, SoundCategory.RECORDS, 3.0F, pitch, false);

				//Trigger a note particle at location
				world.spawnParticle(EnumParticleTypes.NOTE, x, y + 0.7D, z, playingNoteId / 24.0D, 0.0D, 0.0D);
			}
		}
	}

	//TODO: Access Transformer - Read this from the instrument list in BlockNote

	private static final List<SoundEvent> INSTRUMENTS = Lists.newArrayList(SoundEvents.BLOCK_NOTE_HARP, SoundEvents.BLOCK_NOTE_BASEDRUM, SoundEvents.BLOCK_NOTE_SNARE, SoundEvents.BLOCK_NOTE_HAT, SoundEvents.BLOCK_NOTE_BASS, SoundEvents.BLOCK_NOTE_FLUTE, SoundEvents.BLOCK_NOTE_BELL, SoundEvents.BLOCK_NOTE_GUITAR, SoundEvents.BLOCK_NOTE_CHIME, SoundEvents.BLOCK_NOTE_XYLOPHONE);

	private SoundEvent getInstrument(int eventId)
	{
		if (eventId < 0 || eventId >= INSTRUMENTS.size())
		{
			eventId = 0;
		}

		return INSTRUMENTS.get(eventId);
	}

	public SequencerSet getSequencerSet()
	{
		return sequencerSet;
	}
}
