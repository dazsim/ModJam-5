package org.atomicworkshop.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.atomicworkshop.ConductorMod;
import org.atomicworkshop.libraries.CollisionMaths;
import org.atomicworkshop.tiles.TileEntitySequencer;
import javax.annotation.Nullable;

public class BlockSequencer extends BlockHorizontal implements ITileEntityProvider
{
	public BlockSequencer() {
		super(new SequencerMaterial());

		final IBlockState defaultState = blockState.getBaseState()
				.withProperty(FACING, EnumFacing.NORTH);
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
		return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
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

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
	{
		final TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			final TileEntitySequencer teSequencer = (TileEntitySequencer)tileEntity;
			teSequencer.stopPlaying();
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
	{
		//if (!worldIn.isRemote) return;

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
	@Deprecated
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param)
	{
		final TileEntitySequencer tileEntity = getTileEntity(world, pos);
		if (tileEntity == null) return false;

		return tileEntity.receiveClientEvent(id, param);
	}

	private Vec3d calculateSlopeHit(BlockPos pos, EntityPlayer playerIn)
	{
		final Vec3d headPosition = CollisionMaths.getPlayerHeadPosition(playerIn);
		final Vec3d lookVector = CollisionMaths.getPlayerLookVector(playerIn);

		//TODO: rotate Origin according to block direction
		final Vec3d planeOrigin = new Vec3d(pos.getX(), pos.getY() + 1/16.0f, pos.getZ());
		final Vec3d topCorner = new Vec3d(pos.getX() + 15.75f / 16.0f, pos.getY() + 7.5f / 16.0f, pos.getZ());
		final Vec3d bottomCorner = new Vec3d(pos.getX(), pos.getY() + 1/16.0f, pos.getZ() + 1);

		final Vec3d u = bottomCorner.subtract(planeOrigin);
		final Vec3d v = topCorner.subtract(planeOrigin);
		final Vec3d planeNormal = u.crossProduct(v);

		final Vec3d vector3d = CollisionMaths.intersectionLinePlane(headPosition, lookVector, planeOrigin, planeNormal);

		if (vector3d == null) {
			ConductorMod.logger.info("player missed");
			return null;
		} else
		{
			final Vec3d hitVector = vector3d.subtract(pos.getX(), pos.getY(), pos.getZ());
			ConductorMod.logger.info("player clicked at {},{},{}", hitVector.x, hitVector.y, hitVector.z);
			return hitVector;
		}
	}

	private TileEntitySequencer getTileEntity(IBlockAccess world, BlockPos pos) {
		final TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			return (TileEntitySequencer)tileEntity;
		}
		return null;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {

    }
}
