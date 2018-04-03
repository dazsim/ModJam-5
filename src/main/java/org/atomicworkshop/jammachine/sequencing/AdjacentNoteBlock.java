package org.atomicworkshop.jammachine.sequencing;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.world.NoteBlockEvent.Instrument;

public class AdjacentNoteBlock
{
	private Instrument instrument = null;
	private final EnumFacing direction;

	AdjacentNoteBlock(EnumFacing direction)
	{
		this.direction = direction;
	}

	boolean canPlay()
	{
		return instrument != null;
	}

	public Instrument getInstrument()
	{
		return instrument;
	}

	public void setInstrument(Instrument instrument) {
		this.instrument = instrument;
	}

	public EnumFacing getDirection()
	{
		return direction;
	}
}
