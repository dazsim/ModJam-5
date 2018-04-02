package org.atomicworkshop.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.Vector3d;
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);
	}

	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
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

	private TileEntitySequencer getTileEntity(IBlockAccess world, BlockPos pos) {
		final TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			return (TileEntitySequencer)tileEntity;
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		final Vector3d headPosition = CollisionMaths.getPlayerHeadPosition(playerIn);
		final Vector3d lookVector = CollisionMaths.getPlayerLookVector(playerIn, headPosition);

		//TODO: rotate Origin according to block direction
		final Vector3d planeOrigin = new Vector3d();
		planeOrigin.x = pos.getX();
		planeOrigin.y = pos.getY() + 1/16.0f;
		planeOrigin.z = pos.getZ();

		final Vector3d topCorner = new Vector3d();
		topCorner.x = pos.getX();
		topCorner.y = pos.getY() + 7.5f/16.0f;
		topCorner.z = pos.getZ() + 15.75f/16.0f;

		final Vector3d bottomCorner = new Vector3d();
		bottomCorner.x = pos.getX() + 1;
		bottomCorner.y = pos.getY() + 1/16.0f;
		bottomCorner.z = pos.getZ();

		final Vector3d planeNormal = new Vector3d();
		final Vector3d u = CollisionMaths.subVector3d(topCorner, planeOrigin);
		final Vector3d v = CollisionMaths.subVector3d(bottomCorner, planeOrigin);

		planeNormal.x = (u.y * v.z) - (u.z * v.y);
		planeNormal.y = (u.z * v.x) - (u.x * v.z);
		planeNormal.z = (u.x * v.y) - (u.y * v.x);

		final Vector3d vector3d = CollisionMaths.intersectionLinePlane(headPosition, lookVector, planeOrigin, planeNormal);

		if (vector3d == null) {
			ConductorMod.logger.info("player missed");
		} else
		{
			ConductorMod.logger.info("player clicked at {},{},{}", vector3d.x - pos.getX(), vector3d.y- pos.getY(), vector3d.z- pos.getZ());
		}

		return true;
	}

	@Override
	public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn)
    {

    }
}
