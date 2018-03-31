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
				if (playingSequence.getSequencerSet().equals(sequencerSet)) return;
			}

			playingSequences.add(new PlayingSequence(sequencerSet));
		}
	}

	public static void stopPlaying(SequencerSet sequencerSet) {
		if (sequencerSet == null) return;
		synchronized (sequenceLock) {
			playingSequences.removeIf(playingSequence -> playingSequence.getSequencerSet().equals(sequencerSet));
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
				if (playingSequence.getNextTickTime() >= currentTime) {
					playingSequence.setNextTickTime(currentTime + (playingSequence.getBeatsPerMinute() / 60 / 1000));
					playingSequence.playNextInterval();
				}
			}
		}
	}
}

