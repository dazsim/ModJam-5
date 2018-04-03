package org.atomicworkshop.jammachine.sequencing;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.JamMachineMod;
import java.util.UUID;

public class JamController extends SequencerSet
{
	private final World world;
	private final BlockPos pos;

	public JamController(World world, BlockPos pos)
	{
		super(UUID.randomUUID());
		this.world = world;
		this.pos = pos;
	}

	@Override
	public void addSequencer(Sequencer sequencer)
	{
		if (sequencer.getWorld().provider.getDimension() != world.provider.getDimension()) {
			JamMachineMod.logger.warn("SequencerSet world {} vs Sequencer world {}",
					world.provider.getDimension(),
					sequencer.getWorld().provider.getDimension()
			);
		}

		super.addSequencer(sequencer);
	}

	public World getWorld()
	{
		return world;
	}

	public BlockPos getPos()
	{
		return pos;
	}
}
