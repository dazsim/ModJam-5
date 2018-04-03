package org.atomicworkshop.jammachine.sequencing;

import com.google.common.collect.Lists;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import java.util.UUID;

public class ControllerPattern
{
	UUID sequencerId;
	Sequencer locatedSequencer;
	BlockPos expectedSequencerLocation;
	List<Byte> patternAtInterval = Lists.newArrayList();
}
