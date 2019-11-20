package org.atomicworkshop.jammachine.sequencing;

import com.google.common.collect.ImmutableList;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.NoteBlockEvent.Play;

class PlayingSequence
{
    private final SequencerSet sequencerSet;
    private long nextIntervalMilliseconds;


    PlayingSequence(SequencerSet sequencerSet)
    {
        this.sequencerSet = sequencerSet;
    }

    long getBeatsPerMinute()
    {
        return sequencerSet.getBeatsPerMinute();
    }

    long getNextIntervalMillis()
    {
        return nextIntervalMilliseconds;
    }

    void setNextIntervalMillis(long milliseconds)
    {
        nextIntervalMilliseconds = milliseconds;
    }

    void playNextInterval()
    {
        for (final Sequencer sequencer : sequencerSet)
        {
            if (sequencer.incrementInterval()) {
                sequencerSet.marchInterval();
                final int currentPatternIndexForSequencer = sequencerSet.getCurrentPatternIndexForSequencer(sequencer.getId());
                sequencer.setCurrentPatternIndex(currentPatternIndexForSequencer);
            }

            playSequencer(sequencer);
        }
    }

    private void playSequencer(Sequencer sequencer)
    {
        final ImmutableList<AdjacentNoteBlock> availableNoteBlocks = sequencer.getAvailableNoteBlocks();
        if (availableNoteBlocks.isEmpty()) return;

        final Pattern currentPattern = sequencer.getCurrentPattern();
        for (final Byte pitchToPlay : currentPattern.getPitchesAtInterval(sequencer.getCurrentInterval()))
        {
            final int noteBlockSearch = sequencer.incrementNoteBlockNumber();

            final AdjacentNoteBlock noteBlock = availableNoteBlocks.get(noteBlockSearch);

            MusicPlayer.playNote(sequencer, noteBlock, pitchToPlay);
        }
    }

    SequencerSet getSequencerSet()
    {
        return sequencerSet;
    }
}