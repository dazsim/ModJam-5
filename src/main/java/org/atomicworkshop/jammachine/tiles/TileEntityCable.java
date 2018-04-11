package org.atomicworkshop.jammachine.tiles;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.Reference.NBT;
import org.atomicworkshop.jammachine.blocks.BlockCable;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class TileEntityCable extends TileEntity{
	public Boolean FLOOR = Boolean.FALSE;
	public Boolean CEILING = Boolean.FALSE;
	public Boolean NORTH = Boolean.FALSE;
	public Boolean SOUTH = Boolean.FALSE;
	public Boolean EAST = Boolean.FALSE;
	public Boolean WEST = Boolean.FALSE;
	
	
	
	
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
        Block b = this.getWorld().getBlockState(pos).getBlock();
        this.getWorld().setBlockState(pos, this.getWorld().getBlockState(pos)
        		.withProperty(BlockCable.FLOOR, this.FLOOR).withProperty(BlockCable.CEILING, this.CEILING)
        		.withProperty(BlockCable.NORTH, this.NORTH).withProperty(BlockCable.SOUTH, this.SOUTH)
        		.withProperty(BlockCable.EAST, this.EAST).withProperty(BlockCable.WEST, this.WEST)
        		);
       

        
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        JamMachineMod.logger.info("writing cable to NBT");

        super.writeToNBT(compound);

        Collection<IProperty<?>> props  = this.getWorld().getBlockState(pos).getBlock().getActualState(this.getWorld().getBlockState(pos), world, pos).getPropertyKeys();
        Iterator iprop = props.iterator();
        while(iprop.hasNext())
        {
        	PropertyBool propB = (PropertyBool)iprop.next();
        	if (propB.getName().equals(BlockCable.FLOOR.getName()))
        	{
        		this.FLOOR = propB.equals(true) || false;
        	}
        	if (propB.getName().equals(BlockCable.CEILING.getName()))
        	{
        		this.CEILING = propB.equals(true) || false;
        	}
        }
        compound.setBoolean(NBT.floor, FLOOR);
        compound.setBoolean(NBT.ceiling, CEILING);
        compound.setBoolean(NBT.north, NORTH);
        compound.setBoolean(NBT.south, SOUTH);
        compound.setBoolean(NBT.east, EAST);
        compound.setBoolean(NBT.west,WEST);
        
      

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
    	}
    	if (facing.equals(EnumFacing.DOWN))
    	{
    		FLOOR = state;
    	}
    	if (facing.equals(EnumFacing.NORTH))
    	{
    		NORTH = state;
    	}
    	if (facing.equals(EnumFacing.SOUTH))
    	{
    		SOUTH = state;
    	}
    	if (facing.equals(EnumFacing.EAST))
    	{
    		EAST = state;
    	}
    	if (facing.equals(EnumFacing.WEST))
    	{
    		WEST = state;
    	}
    	
    	this.markDirty();
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
