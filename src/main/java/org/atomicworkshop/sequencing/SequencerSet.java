package org.atomicworkshop.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.atomicworkshop.ConductorMod;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.UUID;
import java.util.function.Consumer;

public class SequencerSet implements Iterable<Sequencer>
{
	private int beatsPerMinute;
	private final List<Sequencer> sequencers = Lists.newArrayList();
	private final World world;
	private final UUID id;

	public SequencerSet(World world, UUID id)
	{
		this.world = world;
		this.id = id;
	}

	public void addSequencer(Sequencer sequencer)
	{
		if (sequencer.getWorld().provider.getDimension() != world.provider.getDimension()) {
			ConductorMod.logger.warn("SequencerSet world {} vs Sequencer world {}");
		}

		sequencers.add(sequencer);
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

	public long getBeatsPerMinute()
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

	public UUID getId()
	{
		return id;
	}
}
