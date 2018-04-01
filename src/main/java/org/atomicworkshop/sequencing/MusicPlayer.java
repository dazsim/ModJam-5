package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
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

			PlayingSequence e = new PlayingSequence(sequencerSet);
			final long currentTimeMillis = System.nanoTime() / 1000000;
			e.setNextIntervalMillis(currentTimeMillis + (250 / (e.getBeatsPerMinute() / 60)));

			for (final Sequencer sequencer : sequencerSet)
			{
				sequencer.setCurrentInterval(0);
				sequencer.setCurrentPatternIndex(sequencer.getPendingPatternIndex());
			}

			playingSequences.add(e);
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
		Minecraft minecraft = Minecraft.getMinecraft();
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
				final long nextIntervalMillis = playingSequence.getNextIntervalMillis();
				if (currentTimeMillis >= nextIntervalMillis) {
					final long millisToNextInterval = 250 / (playingSequence.getBeatsPerMinute() / 60);
					playingSequence.setNextIntervalMillis(currentTimeMillis + millisToNextInterval);
					playingSequence.playNextInterval();
				}
			}
		}
	}
}

