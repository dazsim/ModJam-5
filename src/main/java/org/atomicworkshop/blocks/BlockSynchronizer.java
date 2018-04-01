package org.atomicworkshop.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.atomicworkshop.tiles.TileEntitySequencer;

import javax.annotation.Nullable;

import static net.minecraft.block.BlockHorizontal.FACING;

public class BlockSynchronizer extends Block implements ITileEntityProvider
{
	public BlockSynchronizer() {
		super(new MachineMaterial());

		final IBlockState defaultState = blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH);
		setDefaultState(defaultState);
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, FACING);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(FACING)
				.getHorizontalIndex();
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState()
				.withProperty(FACING, EnumFacing.getHorizontal(meta));
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
	                                        float hitX, float hitY, float hitZ, int meta,
	                                        EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySequencer();
	}
}
