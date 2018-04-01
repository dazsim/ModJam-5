package org.atomicworkshop.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MachineMaterial extends Material
{
	public MachineMaterial()
	{
		super(MapColor.BROWN);
		this.setNoPushMobility();
		this.setImmovableMobility();
	}
}
