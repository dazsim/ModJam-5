package org.atomicworkshop.jammachine.items;

import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.blocks.BlockCable;
import org.atomicworkshop.jammachine.libraries.BlockLibrary;
import org.atomicworkshop.jammachine.tiles.TileEntityCable;

import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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
		
			
		
			JamMachineMod.logger.info(world.getBlockState(pos.offset(side)).getBlock().toString());
			if (world.getBlockState(pos).getBlock() instanceof BlockCable)
			{
				//we are clicking an existing block
				if (world.getBlockState(pos.offset(side.getOpposite())).getBlock().isSideSolid(world.getBlockState(pos.offset(side.getOpposite())), world, pos, side))
				{
					//is block placing on solid
					JamMachineMod.logger.info("Mod BLOCK"+side.toString());
					TileEntity t = world.getTileEntity(pos);
					TileEntityCable t2 = (TileEntityCable)t;
					logTE(t2);
					//NORTH
					
						
						if (!t2.hasCable(side.getOpposite()))
						{
							t2.setCable(side.getOpposite(), true);
							//world.setBlockState(pos, BlockLibrary.cable.getActualState(null, world, pos), 3);
							world.markAndNotifyBlock(pos, null, world.getBlockState(pos), BlockLibrary.cable.getActualState(null,world,pos), 3);
							JamMachineMod.logger.info("setting block"+side.toString());
							logTE(t2);
							return e.SUCCESS;
						}
					
					
				}
			}
			if (world.getBlockState(pos.offset(side)).getBlock() instanceof BlockAir)
			{
				if (side.equals(EnumFacing.UP))
				{
					//this should create a new cable at coordinates
					JamMachineMod.logger.info("Place BLOCK");
					world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.FLOOR,Boolean.TRUE), 3);
					
					world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.FLOOR,Boolean.TRUE), 3);
					TileEntity t = world.getTileEntity(pos.offset(side));
					TileEntityCable t2 = (TileEntityCable)t;
					t2.setCable(side.getOpposite(), true);
					
					logTE(t2);
					return e.SUCCESS;
				} else
				if (side.equals(EnumFacing.DOWN))
				{
					//this should create a new cable at coordinates
					JamMachineMod.logger.info("Place BLOCK");
					world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.CEILING,Boolean.TRUE), 3);
					
					world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.FLOOR,Boolean.TRUE), 3);
					TileEntity t = world.getTileEntity(pos.offset(side));
					TileEntityCable t2 = (TileEntityCable)t;
					t2.setCable(side.getOpposite(), true);
					
					logTE(t2);
					return e.SUCCESS;
				}
				else
				if (side.equals(EnumFacing.NORTH))
				{
					//this should create a new cable at coordinates
					JamMachineMod.logger.info("Place BLOCK");
					world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.NORTH,Boolean.TRUE), 3);
					
					world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.NORTH,Boolean.TRUE), 3);
					TileEntity t = world.getTileEntity(pos.offset(side));
					TileEntityCable t2 = (TileEntityCable)t;
					t2.setCable(side.getOpposite(), true);
					
					logTE(t2);
					return e.SUCCESS;
				}
				else
					if (side.equals(EnumFacing.SOUTH))
					{
						//this should create a new cable at coordinates
						JamMachineMod.logger.info("Place BLOCK");
						world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.SOUTH,Boolean.TRUE), 3);
						
						world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.SOUTH,Boolean.TRUE), 3);
						TileEntity t = world.getTileEntity(pos.offset(side));
						TileEntityCable t2 = (TileEntityCable)t;
						t2.setCable(side.getOpposite(), true);
						
						logTE(t2);
						return e.SUCCESS;
					}
					else
					if (side.equals(EnumFacing.EAST))
					{
						//this should create a new cable at coordinates
						JamMachineMod.logger.info("Place BLOCK");
						world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.EAST,Boolean.TRUE), 3);
						
						world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.EAST,Boolean.TRUE), 3);
						TileEntity t = world.getTileEntity(pos.offset(side));
						TileEntityCable t2 = (TileEntityCable)t;
						t2.setCable(side.getOpposite(), true);
						
						logTE(t2);
						return e.SUCCESS;
					}
					else
						if (side.equals(EnumFacing.WEST))
						{
							//this should create a new cable at coordinates
							JamMachineMod.logger.info("Place BLOCK");
							world.setBlockState(pos.offset(side), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.WEST,Boolean.TRUE), 3);
							
							world.markAndNotifyBlock(pos.offset(side), null, world.getBlockState(pos.offset(side)), BlockLibrary.cable.getDefaultState().withProperty(BlockLibrary.cable.WEST,Boolean.TRUE), 3);
							TileEntity t = world.getTileEntity(pos.offset(side));
							TileEntityCable t2 = (TileEntityCable)t;
							t2.setCable(side.getOpposite(), true);
							
							logTE(t2);
							return e.SUCCESS;
						}
			}
		
		return e.FAIL;
		
	}
	public void logTE(TileEntityCable te)
	{
		JamMachineMod.logger.info("TE Status: ");
		JamMachineMod.logger.info("UP: "+te.hasCable(EnumFacing.UP).toString());
		JamMachineMod.logger.info("DOWN: "+te.hasCable(EnumFacing.DOWN).toString());
		JamMachineMod.logger.info("NORTH: "+te.hasCable(EnumFacing.NORTH).toString());
		JamMachineMod.logger.info("SOUTH: "+te.hasCable(EnumFacing.SOUTH).toString());
		JamMachineMod.logger.info("EAST: "+te.hasCable(EnumFacing.EAST).toString());
		JamMachineMod.logger.info("WEST: "+te.hasCable(EnumFacing.WEST).toString());
	}
}