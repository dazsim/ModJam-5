package org.atomicworkshop.jammachine.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.NoteBlockInstrument;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.NoteBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber
public final class MusicPlayer
{
    private static final List<PlayingSequence> playingSequences = Lists.newArrayList();
    private static final Object sequenceLock = new Object();
    private static long lastUpdateIndex = Long.MIN_VALUE;

    public static void playSong(SequencerSet sequencerSet)
    {
        if (sequencerSet == null) return;
        sequencerSet.updateBpm();
        if (sequencerSet.getBeatsPerMinute() == 0) return;
        final long currentTimeMillis = System.nanoTime() / 1000000;

        synchronized(sequenceLock) {
            for (final PlayingSequence playingSequence : playingSequences)
            {
                if (playingSequence.getSequencerSet().getId().equals(sequencerSet.getId())) return;
            }

            final PlayingSequence e = new PlayingSequence(sequencerSet);

            e.setNextIntervalMillis(currentTimeMillis + (250 / (e.getBeatsPerMinute() / 60)));

            for (final Sequencer sequencer : sequencerSet)
            {
                sequencer.reset();
            }

            playingSequences.add(e);
        }
    }

    public static void stopPlaying(UUID sequencerSetId) {
        if (sequencerSetId == null) return;
        synchronized (sequenceLock) {
            playingSequences.removeIf(playingSequence -> playingSequence.getSequencerSet().getId().equals(sequencerSetId));
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.RenderTickEvent clientTickEvent) {
        if (clientTickEvent.phase != TickEvent.Phase.START) return;
        final Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.isGamePaused()) return;
        if (minecraft.world == null) {
            synchronized (sequenceLock) {
                playingSequences.clear();
            }
            return;
        }

        final long currentTimeMillis = System.nanoTime() / 1000000;

        synchronized (sequenceLock) {
            if (playingSequences.isEmpty()) return;

            for (final PlayingSequence playingSequence : playingSequences)
            {
                if (playingSequence.getSequencerSet().getBeatsPerMinute() == 0) {
                    continue;
                }
                final long nextIntervalMillis = playingSequence.getNextIntervalMillis();
                if (currentTimeMillis >= nextIntervalMillis) {
                    final long millisToNextInterval = 250 / (playingSequence.getBeatsPerMinute() / 60);
                    playingSequence.setNextIntervalMillis(currentTimeMillis + millisToNextInterval);
                    playingSequence.playNextInterval();
                }
            }
        }
    }

    public static void playNote(Sequencer sequencer, AdjacentNoteBlock noteBlock, Byte pitchToPlay)
    {
        final BlockPos sequencerBlockPos = sequencer.getBlockPos();
        final BlockPos pos = sequencerBlockPos.offset(noteBlock.getDirection());

        //Schedule sound to be played
        final World world = sequencer.getWorld();
        final NoteBlockEvent.Play e = new NoteBlockEvent.Play(world, pos, world.getBlockState(pos), pitchToPlay, noteBlock.getInstrument());
        if (MinecraftForge.EVENT_BUS.post(e)) return;

        final int playingInstrumentId = e.getInstrument().ordinal();
        final int playingNoteId = e.getVanillaNoteId();
        final float pitch = (float) StrictMath.pow(2.0D, (playingNoteId - 12) / 12.0D);
        final SoundEvent instrument = getInstrument(playingInstrumentId);

        final double x = pos.getX() + 0.5;
        final double y = pos.getY() + 0.5;
        final double z = pos.getZ() + 0.5;
        world.playSound(x, y, z, instrument, SoundCategory.RECORDS, 3.0F, pitch, false);

        //Trigger a note particle at location
        world.addParticle(ParticleTypes.NOTE, x, y + 0.7D, z, playingNoteId / 24.0D, 0.0D, 0.0D);
    }

    private static SoundEvent getInstrument(int eventId)
    {
        final NoteBlockInstrument[] values = NoteBlockInstrument.values();

        if (eventId < 0 || eventId >= values.length)
        {
            eventId = 0;
        }

        return values[eventId].getSound();
    }

    private static final List<Sequencer> trackedSequencers = Lists.newArrayList();
    private static final List<JamController> trackedControllers = Lists.newArrayList();
    private static final Object trackingLock = new Object();

    public static void stopTrackingSequencerAt(World worldIn, BlockPos pos)
    {
        synchronized (trackingLock)
        {
            lastUpdateIndex++;
            trackedSequencers.removeIf(sequencer ->
                    sequencer.getWorld().dimension.getType().getId() == worldIn.dimension.getType().getId() &&
                            pos.equals(sequencer.getBlockPos()));
        }
    }

    public static void stopTrackingControllerAt(World worldIn, BlockPos pos)
    {
        synchronized (trackingLock)
        {
            lastUpdateIndex++;
            trackedControllers.removeIf(controller ->
                    controller.getWorld().dimension.getType().getId() == worldIn.dimension.getType().getId() &&
                            pos.equals(controller.getPos()));
        }
    }

    public static void startTracking(JamController controller)
    {
        synchronized (trackingLock)
        {
            lastUpdateIndex++;
            for (final JamController trackedController : trackedControllers)
            {
                if (trackedController.getId().equals(controller.getId())) {
                    return;
                }
            }
            trackedControllers.add(controller);
        }
    }

    public static void startTracking(Sequencer sequencer)
    {
        synchronized (trackingLock)
        {
            lastUpdateIndex++;
            for (final Sequencer trackedSequencer : trackedSequencers)
            {
                if (trackedSequencer.getId().equals(sequencer.getId())) {
                    return;
                }
            }
            trackedSequencers.add(sequencer);
        }
    }

    public static Sequencer getSequencerById(UUID sequencerId)
    {
        synchronized (trackingLock)
        {
            for (final Sequencer trackedSequencer : trackedSequencers)
            {
                if (trackedSequencer.getId().equals(sequencerId)) {
                    return trackedSequencer;
                }
            }
        }
        return null;
    }

    public static long getLastUpdateIndex()
    {
        synchronized (trackingLock)
        {
            return lastUpdateIndex;
        }
    }
}
