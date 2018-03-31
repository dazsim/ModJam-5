package org.atomicworkshop.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class SequencerMaterial extends Material
{
	public SequencerMaterial()
	{
		super(MapColor.BROWN);
		this.setNoPushMobility();
		this.setImmovableMobility();
	}
}
