package org.atomicworkshop.jammachine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.tiles.TileEntitySequencer;
import org.atomicworkshop.jammachine.util.CollisionMaths;
import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockCable extends Block 
{
	
	/* cable placed on floor */
	 public static final PropertyBool FLOOR = PropertyBool.create("floor");
	 /* cable placed on floor */
	 public static final PropertyBool CEILING = PropertyBool.create("ceiling");
	 /* cable placed on floor */
	 
	 //public static final PropertyBool NORTH = PropertyBool.create("north");
	 /* cable placed on floor */
	 //public static final PropertyBool SOUTH = PropertyBool.create("south");
	 /* cable placed on floor */
	 //public static final PropertyBool EAST = PropertyBool.create("east");
	 /* cable placed on floor */
	 //public static final PropertyBool WEST = PropertyBool.create("west");
	 
	 
	 
	private final AxisAlignedBB boundingBox = new AxisAlignedBB(0, 0, 0, 1, 0.2, 1);

	public BlockCable() {
		super(new MachineMaterial());
		//blockState.validateProperty(block, property)
		//final IBlockState defaultState = blockState.getBaseState().withProperty(NORTH, Boolean.FALSE).withProperty(EAST, Boolean.FALSE).withProperty(SOUTH, Boolean.FALSE).withProperty(WEST, Boolean.FALSE).withProperty(FLOOR, Boolean.FALSE).withProperty(CEILING, Boolean.FALSE);
		
		final IBlockState defaultState = this.blockState.getBaseState().withProperty(FLOOR, Boolean.FALSE).withProperty(CEILING, Boolean.FALSE);
		
		setDefaultState(defaultState);
	}

	@Override
	public boolean shouldCheckWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
	{
		//Disabling weak power checks here prevents Note Blocks from being fired when the sequencer receives a redstone
		//signal.
		return false;
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return boundingBox;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0; //TODO : this will change as i add support for cables connecting to each other. or not
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
	                                        float hitX, float hitY, float hitZ, int meta,
	                                        EntityLivingBase placer, EnumHand hand)
	{
		/*check if there is already a cable in this block*/
		if (world.getBlockState(pos).getBlock() instanceof BlockCable)
		{
			/*check if there is already a cable on this surface*/
			if (facing.equals(facing.DOWN))
			{
				if (world.getBlockState(pos).getProperties().containsKey(FLOOR))
				{
					return (world.getBlockState(pos));
				}  else
				{
					return (world.getBlockState(pos).withProperty(this.FLOOR, true));
				}
			}
			if (facing.equals(facing.UP))
			{
				if (world.getBlockState(pos).getProperties().containsKey(CEILING))
				{
					return (world.getBlockState(pos));
				} else
				{
					/*valid placement. make sure there is a valid block above this one*/
					if (world.getBlockState(pos).getBlock().isFullBlock(world.getBlockState(pos)))
					{
						return (world.getBlockState(pos).withProperty(this.CEILING, true));
					}
				}
			}
		}
		if (facing.DOWN.equals(facing))
		{
			return getDefaultState().withProperty(this.FLOOR, true);
		}
		return getDefaultState(); //TODO this will change as i add support for cables connecting when you place them
	}
/*
	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySequencer();
	}*/

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		/*final TileEntitySequencer teSequencer = getTileEntity(worldIn, pos);
		if (teSequencer != null) {
			if (teSequencer.getHasCard())
			{
				teSequencer.ejectCard(pos.getX(), pos.getY(), pos.getZ());
			}

			teSequencer.stopPlaying();
			MusicPlayer.stopTrackingSequencerAt(worldIn, pos);
		}*/
		//notify adjcent blocks that this block is broken
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		boolean isPowered = false;
		for (final EnumFacing value : EnumFacing.VALUES)
		{
			isPowered = worldIn.isBlockPowered(pos.offset(value));
			if (isPowered) break;
		}

		final TileEntitySequencer tileEntity = getTileEntity(worldIn, pos);
		if (tileEntity == null) return;

		tileEntity.notifyPowered(isPowered);
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state)
	{
		return false; 
	}
	@Override
	public boolean isTopSolid(IBlockState state)
	{
		return true;
		//return false; 
	}
	
	
	
	@Override
	@Deprecated
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		final TileEntitySequencer tileEntity = getTileEntity(world, pos);
		if (tileEntity == null) return false;

		return tileEntity.receiveClientEvent(id, param);
	}

	public TileEntitySequencer getTileEntity(IBlockAccess world, BlockPos pos) {
		final TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			return (TileEntitySequencer)tileEntity;
		}
		JamMachineMod.logger.error("No Tile entity found at block location? {}", pos);
		return null;
	}

	@SuppressWarnings("ChainOfInstanceofChecks")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		/*
		 * This is where we handle interaction with the cable and adjcent cables
		 * 
		 */
	    

	    return false;
    }


}

