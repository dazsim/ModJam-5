package org.atomicworkshop.jammachine.tiles;

import javax.annotation.Nullable;

import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.Reference.NBT;
import org.atomicworkshop.jammachine.blocks.BlockCable;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityCable extends TileEntity{
	
	public Boolean FLOOR = Boolean.FALSE;
	public Boolean CEILING = Boolean.FALSE;
	public Boolean NORTH = Boolean.FALSE;
	public Boolean SOUTH = Boolean.FALSE;
	public Boolean EAST = Boolean.FALSE;
	public Boolean WEST = Boolean.FALSE;
	//This is where it gets silly
	public Boolean FLOORNorth = Boolean.FALSE;
	public Boolean FLOORSouth = Boolean.FALSE;
	public Boolean FLOOREast = Boolean.FALSE;
	public Boolean FLOORWest = Boolean.FALSE;
	public Boolean CEILINGNorth = Boolean.FALSE;
	public Boolean CEILINGSouth = Boolean.FALSE;
	public Boolean CEILINGEast = Boolean.FALSE;
	public Boolean CEILINGWest = Boolean.FALSE;
	public Boolean NORTHFloor = Boolean.FALSE;
	public Boolean NORTHCeiling = Boolean.FALSE;
	public Boolean NORTHEast = Boolean.FALSE;
	public Boolean NORTHWest = Boolean.FALSE;
	public Boolean SOUTHFloor = Boolean.FALSE;
	public Boolean SOUTHCeiling = Boolean.FALSE;
	public Boolean SOUTHEast = Boolean.FALSE;
	public Boolean SOUTHWest = Boolean.FALSE;
	public Boolean EASTFloor = Boolean.FALSE;
	public Boolean EASTCeiling = Boolean.FALSE;
	public Boolean EASTNorth = Boolean.FALSE;
	public Boolean EASTSouth = Boolean.FALSE;
	public Boolean WESTFloor = Boolean.FALSE;
	public Boolean WESTCeiling = Boolean.FALSE;
	public Boolean WESTNorth = Boolean.FALSE;
	public Boolean WESTSouth = Boolean.FALSE;
	
	public boolean[][] adjcentCables;
	//adjcentCables = new boolean[6][4];
	
	
	
	@Override
    public void readFromNBT(NBTTagCompound compound)
    {
        JamMachineMod.logger.info("read cable from NBT");

        super.readFromNBT(compound);
        
        FLOOR = Boolean.valueOf(compound.getBoolean(NBT.floor));
        CEILING = Boolean.valueOf(compound.getBoolean(NBT.ceiling));
        NORTH = Boolean.valueOf(compound.getBoolean(NBT.north));
        SOUTH = Boolean.valueOf(compound.getBoolean(NBT.south));
        EAST = Boolean.valueOf(compound.getBoolean(NBT.east));
        WEST = Boolean.valueOf(compound.getBoolean(NBT.west));
        
        FLOORNorth = Boolean.valueOf(compound.getBoolean(NBT.floornorth));
        FLOORSouth = Boolean.valueOf(compound.getBoolean(NBT.floorsouth));
        FLOOREast = Boolean.valueOf(compound.getBoolean(NBT.flooreast));
        FLOORWest = Boolean.valueOf(compound.getBoolean(NBT.floorwest));
        
        /*Block b = this.getWorld().getBlockState(pos).getBlock();
        this.getWorld().setBlockState(pos, this.getWorld().getBlockState(pos)
        		.withProperty(BlockCable.FLOOR, this.FLOOR).withProperty(BlockCable.CEILING, this.CEILING)
        		.withProperty(BlockCable.NORTH, this.NORTH).withProperty(BlockCable.SOUTH, this.SOUTH)
        		.withProperty(BlockCable.EAST, this.EAST).withProperty(BlockCable.WEST, this.WEST)
        		);*/
        ItemLibrary.heldCable.logTE(this);
       

        
    }

    public TileEntityCable() {
		super();
		adjcentCables = new boolean[6][4];
		
	}

	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        JamMachineMod.logger.info("writing cable to NBT");

        super.writeToNBT(compound);

        
        compound.setBoolean(NBT.floor, FLOOR);
        compound.setBoolean(NBT.ceiling, CEILING);
        compound.setBoolean(NBT.north, NORTH);
        compound.setBoolean(NBT.south, SOUTH);
        compound.setBoolean(NBT.east, EAST);
        compound.setBoolean(NBT.west,WEST);
        
        compound.setBoolean(NBT.floornorth, FLOORNorth);
        compound.setBoolean(NBT.floornorth, FLOORSouth);
        compound.setBoolean(NBT.floornorth, FLOOREast);
        compound.setBoolean(NBT.floornorth, FLOORWest);

        return compound;
    }
    /* check Tile Entity for cable */
    public Boolean hasCable(EnumFacing facing)
    {
    	//JamMachineMod.logger.info("The Facing is "+facing.toString());
    	
    	if (facing.equals(EnumFacing.UP))
    	{
    		return Boolean.valueOf(CEILING);
    	}
    	if (facing.equals(EnumFacing.DOWN))
    	{
    		return Boolean.valueOf(FLOOR);
    	}
    	if (facing.equals(EnumFacing.NORTH))
    	{
    		return Boolean.valueOf(NORTH);
    	}
    	if (facing.equals(EnumFacing.SOUTH))
    	{
    		return Boolean.valueOf(SOUTH);
    	}
    	if (facing.equals(EnumFacing.EAST))
    	{
    		return Boolean.valueOf(EAST);
    	}
    	if (facing.equals(EnumFacing.WEST))
    	{
    		return Boolean.valueOf(WEST);
    	}
    	return Boolean.FALSE;
    }

    /* check Tile Entity for cable */
    public void setCable(EnumFacing facing,Boolean state)
    {
    	
    	if (facing.equals(EnumFacing.UP))
    	{
    		CEILING = state;
    		this.markDirty();
    		return;
    	}
    	if (facing.equals(EnumFacing.DOWN))
    	{
    		FLOOR = state;
    		this.markDirty();
    		return;
    	}
    	if (facing.equals(EnumFacing.NORTH))
    	{
    		NORTH = state;
    		this.markDirty();
    		return;
    	}
    	if (facing.equals(EnumFacing.SOUTH))
    	{
    		SOUTH = state;
    		this.markDirty();
    		return;
    	}
    	if (facing.equals(EnumFacing.EAST))
    	{
    		EAST = state;
    		this.markDirty();
    		return;
    	}
    	if (facing.equals(EnumFacing.WEST))
    	{
    		WEST = state;
    		this.markDirty();
    		return;
    	}
    }

    /* Set Connections to adjcent cables */
    public void setConnections(EnumFacing facing,Boolean state,World world, BlockPos pos)
    {
    	if (facing.equals(EnumFacing.DOWN))
    	{
    		if (world.getBlockState(pos.offset(EnumFacing.NORTH)).getBlock().isAir(world.getBlockState(pos.offset(EnumFacing.NORTH)), null, pos.offset(EnumFacing.NORTH)))
    		{
    			//adjcent is AIR, Check for wrap around(obtuse connection)
    		} else if ((world.getBlockState(pos.offset(EnumFacing.NORTH))).getBlock().hasTileEntity()) 
    		{
	    		if (world.getTileEntity(pos.offset(EnumFacing.NORTH)) instanceof TileEntityCable)
	    		{
	    			TileEntityCable te = (TileEntityCable)world.getTileEntity(pos.offset(EnumFacing.NORTH));
	    			//adjcent is cable, Check for connecting cable
	    			if (te.FLOOR.booleanValue())
	    			{
	    				//set both cables connection
	    				te.FLOORSouth = true;
	    				this.FLOORNorth = true;
	    				this.markDirty();
	    				te.markDirty();
	    				return;
	    			}
	    		}
    		}
    	}
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket()
    {
        final NBTTagCompound updateTag = getUpdateTag();
        return new SPacketUpdateTileEntity(pos, 0, updateTag);
    }

    @Override
    public NBTTagCompound getUpdateTag()
    {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        
        handleUpdateTag(pkt.getNbtCompound());

        
    }
	
}
