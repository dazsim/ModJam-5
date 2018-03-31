package org.atomicworkshop.blocks;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
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

public class BlockSequencer extends BlockHorizontal implements ITileEntityProvider
{
	public BlockSequencer() {
		//FIXME: Create an appropriate material. Needs to be immovable
		super(Material.ANVIL);

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
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySequencer();
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			TileEntitySequencer teSequencer = (TileEntitySequencer)tileEntity;
			teSequencer.stopPlaying();
		}

		super.breakBlock(worldIn, pos, state);
	}
}
