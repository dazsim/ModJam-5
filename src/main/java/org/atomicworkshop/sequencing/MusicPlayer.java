package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import java.util.List;
import java.util.UUID;

@EventBusSubscriber
public final class MusicPlayer
{
	private static final List<PlayingSequence> playingSequences = Lists.newArrayList();
	private static final Object sequenceLock = new Object();

	public static void playSong(SequencerSet sequencerSet)
	{
		if (sequencerSet == null) return;

		synchronized(sequenceLock) {
			for (final PlayingSequence playingSequence : playingSequences)
			{
				if (playingSequence.getSequencerSet().getId().equals(sequencerSet.getId())) return;
			}

			playingSequences.add(new PlayingSequence(sequencerSet));
		}
	}

	public static void stopPlaying(SequencerSet sequencerSet) {
		if (sequencerSet == null) return;
		synchronized (sequenceLock) {
			playingSequences.removeIf(playingSequence -> playingSequence.getSequencerSet().getId().equals(sequencerSet.getId()));
		}
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent clientTickEvent) {
		if (clientTickEvent.phase != Phase.START) return;

		//FIXME: represent times in nanoseconds to avoid a divide.
		final long currentTime = System.nanoTime() / 1000000;

		synchronized (sequenceLock) {
			if (playingSequences.isEmpty()) return;

			for (final PlayingSequence playingSequence : playingSequences)
			{
				final long nextTickTime = playingSequence.getNextTickTime();
				if (currentTime >= nextTickTime) {
					long ticksToNextInterval = playingSequence.getBeatsPerMinute();
					playingSequence.setNextTickTime(currentTime + ticksToNextInterval);
					playingSequence.playNextInterval();
				}
			}
		}
	}

	public static SequencerSet getSequencerSetForWorld(World world, UUID songId)
	{
		return new SequencerSet(world, songId);
	}
}

