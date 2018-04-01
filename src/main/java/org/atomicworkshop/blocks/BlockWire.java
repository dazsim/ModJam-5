package org.atomicworkshop.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.atomicworkshop.libraries.BlockLibrary;

public class BlockWire extends Block
{
	private static final PropertyBool IsNorthConnected = PropertyBool.create("north_connected");
	private static final PropertyBool IsSouthConnected = PropertyBool.create("south_connected");
	private static final PropertyBool IsEastConnected = PropertyBool.create("east_connected");
	private static final PropertyBool IsWestConnected = PropertyBool.create("west_connected");
	private static final PropertyBool IsUpConnected = PropertyBool.create("up_connected");
	private static final PropertyBool IsDownConnected = PropertyBool.create("down_connected");

	public BlockWire()
	{
		super(Material.CIRCUITS);

		final IBlockState defaultState = blockState.getBaseState()
				.withProperty(IsNorthConnected, false)
				.withProperty(IsSouthConnected, false)
				.withProperty(IsEastConnected, false)
				.withProperty(IsWestConnected, false)
				.withProperty(IsUpConnected, false)
				.withProperty(IsDownConnected, false)
				;
		setDefaultState(defaultState);

	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this,
				IsNorthConnected,
				IsSouthConnected,
				IsEastConnected,
				IsWestConnected,
				IsUpConnected,
				IsDownConnected
				);
	}

	@Override
	@Deprecated
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		IBlockState wireState = getDefaultState();
		Block block;
		block = worldIn.getBlockState(pos.north()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsNorthConnected, true);
		}

		block = worldIn.getBlockState(pos.south()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsSouthConnected, true);
		}

		block = worldIn.getBlockState(pos.east()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsEastConnected, true);
		}

		block = worldIn.getBlockState(pos.west()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsWestConnected, true);
		}

		block = worldIn.getBlockState(pos.up()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsUpConnected, true);
		}

		block = worldIn.getBlockState(pos.down()).getBlock();
		if (block.equals(this) || block.equals(BlockLibrary.sequencer) || block.equals(BlockLibrary.synchronizer)) {
			wireState.withProperty(IsDownConnected, true);
		}

		return super.getActualState(state, worldIn, pos);
	}

	@Override
	@Deprecated
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return 0;
	}
}
