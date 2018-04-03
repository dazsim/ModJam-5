package org.atomicworkshop.jammachine.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
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
				sequencer.setCurrentInterval(0);
				sequencer.setCurrentPatternIndex(sequencer.getPendingPatternIndex());
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
	public static void onClientTick(ClientTickEvent clientTickEvent) {
		if (clientTickEvent.phase != Phase.START) return;
		final Minecraft minecraft = Minecraft.getMinecraft();
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


	private static final List<Sequencer> trackedSequencers = Lists.newArrayList();
	private static final List<JamController> trackedControllers = Lists.newArrayList();
	private static final Object trackingLock = new Object();

	public static void stopTrackingSequencerAt(World worldIn, BlockPos pos)
	{
		synchronized (trackingLock)
		{
			lastUpdateIndex++;
			trackedSequencers.removeIf(sequencer ->
					sequencer.getWorld().provider.getDimension() == worldIn.provider.getDimension() &&
							pos.equals(sequencer.getBlockPos()));
		}
	}

	public static void stopTrackingControllerAt(World worldIn, BlockPos pos)
	{
		synchronized (trackingLock)
		{
			lastUpdateIndex++;
			trackedControllers.removeIf(controller ->
					controller.getWorld().provider.getDimension() == worldIn.provider.getDimension() &&
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

