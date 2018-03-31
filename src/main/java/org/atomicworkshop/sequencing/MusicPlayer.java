package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import java.util.List;

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

		final long currentTimeMillis = System.nanoTime() / 1000000;

		synchronized (sequenceLock) {
			if (playingSequences.isEmpty()) return;

			for (final PlayingSequence playingSequence : playingSequences)
			{
				final long nextTickTime = playingSequence.getNextIntervalMillis();
				if (currentTimeMillis >= nextTickTime) {
					//FIXME: This is flat-out wrong.
					final long millisToNextInterval = playingSequence.getBeatsPerMinute();
					playingSequence.setNextIntervalMillis(currentTimeMillis + millisToNextInterval);
					playingSequence.playNextInterval();
				}
			}
		}
	}
}

