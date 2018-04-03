package org.atomicworkshop.jammachine.blocks;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

final class MachineMaterial extends Material
{
	MachineMaterial()
	{
		super(MapColor.BROWN);
		setNoPushMobility();
		setImmovableMobility();
	}
}
