package org.atomicworkshop.jammachine.items;

import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.blocks.BlockCable;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCable extends Item{
	
	public ItemCable()
	{
		
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos,EnumHand hand, EnumFacing side, float hitX,
			float hitY, float hitZ) {
		EnumActionResult e = super.onItemUse(player, world, pos, hand,side, hitX, hitY, hitZ);
		// TODO Auto-generated method stub
		JamMachineMod.logger.debug("Place BLOCK");
		
		/*if (world.getBlockState(pos).getBlock() instanceof BlockAir)
		{
			if (side.equals(EnumFacing.UP))
			{
				//this should create a new cable at coordinates
				world.setBlockState(pos, BlockCable.getStateById(0), 3);
				
			}
		}*/
		return e.SUCCESS;
		
	}
	
}
