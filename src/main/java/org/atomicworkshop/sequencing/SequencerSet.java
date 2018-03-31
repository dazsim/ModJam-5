package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class SequencerSet implements Iterable<Sequencer>
{
	private int beatsPerMinute;
	private final List<Sequencer> sequencers = Lists.newArrayList();
	private final World world;

	public SequencerSet(World world)
	{
		this.world = world;
	}

	public Sequencer addSequencer(BlockPos pos)
	{
		final Sequencer newSequencer = new Sequencer(pos);
		sequencers.add(newSequencer);
		return newSequencer;
	}

	public void updateBpm()
	{
		for (final Sequencer sequencer : sequencers)
		{
			//FIXME: (Steven) solve this properly using a synchronizer block
			if (sequencer.getBeatsPerMinute() > 0) {
				beatsPerMinute = Math.max(sequencer.getBeatsPerMinute(), beatsPerMinute);
			}
		}
	}

	long getBeatsPerMinute()
	{
		return beatsPerMinute;
	}

	@Override
	public Iterator<Sequencer> iterator()
	{
		return sequencers.iterator();
	}

	@Override
	public void forEach(Consumer<? super Sequencer> consumer)
	{
		sequencers.forEach(consumer);
	}

	@Override
	public Spliterator<Sequencer> spliterator()
	{
		return sequencers.spliterator();
	}

	public World getWorld()
	{
		return world;
	}
}
