package org.atomicworkshop.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
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
import org.atomicworkshop.ConductorMod;
import org.atomicworkshop.items.ItemPunchCardWritten;
import org.atomicworkshop.libraries.CollisionMaths;
import org.atomicworkshop.items.ItemPunchCardBlank;
import org.atomicworkshop.tiles.TileEntitySequencer;
import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;

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
		final TileEntitySequencer teSequencer = getTileEntity(worldIn, pos);
		if (teSequencer != null) {
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

	private TileEntitySequencer getTileEntity(IBlockAccess world, BlockPos pos) {
		final TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity instanceof TileEntitySequencer) {
			return (TileEntitySequencer)tileEntity;
		}
		ConductorMod.logger.error("No Tile entity found at block location? {}", pos);
		return null;
	}

	@SuppressWarnings("ChainOfInstanceofChecks")
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
		/*
		 * This is where we handle interaction with the Sequencer
		 * Insert card on right click with card in hand
		 * right click card area in order to remove card
		 * right click buttons to toggle
		 * right click BPM controls to change BPM
		 * 
		 */
	    ConductorMod.logger.info("Sequencer Clicked");
	    final TileEntitySequencer teSequencer = getTileEntity(worldIn, pos);
	    if (teSequencer == null) {
	    	return false;
	    }

	    ItemStack heldItemStack = playerIn.getHeldItemMainhand();
	    if (heldItemStack.isEmpty()) {
	    	heldItemStack = playerIn.getHeldItemOffhand();
	    }
	    final Item heldItem = heldItemStack.getItem();

		if (heldItemStack.isEmpty()) {
			if (hand == EnumHand.OFF_HAND) return false;
			final Vec3d hitVec = calculateSlopeHit(pos, state.getValue(FACING), playerIn);
			if (hitVec == null) return false;
			return teSequencer.checkPlayerInteraction(hitVec.x, hitVec.z, playerIn); 
		}

	    if (heldItem instanceof ItemPunchCardBlank) {
		    //you just inserted a blank card into sequencer. load the sequence onto it?
		    if (teSequencer.getHasCard()) {
			    ConductorMod.logger.debug("Sequencer already has Card");
			    return false;
		    } else {
			    teSequencer.setHasCard(true);
			    heldItemStack.shrink(1);
			    return true;
		    }
	    }

	    if (heldItem instanceof ItemPunchCardWritten) {
			//Attempting to insert a written card.
		    if (teSequencer.getHasCard()) {
			    ConductorMod.logger.debug("Sequencer already has Card");
			    return false;
		    } else {
		    	teSequencer.loadFromCard(heldItemStack);
		    	heldItemStack.shrink(1);
		    	return true;
		    }
	    }

	    return false;
    }

	private Vec3d calculateSlopeHit(BlockPos pos, EnumFacing blockFacing, EntityPlayer player)
	{
		final Vec3d headPosition = CollisionMaths.getPlayerHeadPosition(player);
		final Vec3d lookVector = CollisionMaths.getPlayerLookVector(player);

		//FIXME: rotate Origin according to block direction
		//Steven: My brain is fried and I should be able to solve this with a matrix, but for various reasons
		//I've decided to brute force it for the modjam release. Need to find a server-friendly Matrix4f that
		//can work properly with Vec3d, or find another vector class that works with matrices.
		final Vec3d planeOrigin;
		final Vec3d bottomLeft;
		final Vec3d bottomRightCorner;
		switch (blockFacing)
		{
			case NORTH:
				planeOrigin = new Vec3d(pos.getX() + 1, pos.getY() + 7.5f / 16.0f, pos.getZ() + 15.75f / 16.0f);
				bottomLeft = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ());
				bottomRightCorner = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ());
				break;
			case SOUTH:
				planeOrigin = new Vec3d(pos.getX(), pos.getY() + 7.5f / 16.0f, pos.getZ() + (1 - (15.75f / 16.0f)));
				bottomLeft = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				bottomRightCorner = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				break;
			case WEST:
				planeOrigin = new Vec3d(pos.getX() + 15.75f / 16.0f, pos.getY() + 7.5f / 16.0f, pos.getZ());
				bottomLeft = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ());
				bottomRightCorner = new Vec3d(pos.getX(), pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				break;
			case EAST:
				planeOrigin = new Vec3d(pos.getX() + (1 - (15.75f / 16.0f)), pos.getY() + 7.5f / 16.0f, pos.getZ() + 1);
				bottomLeft = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ() + 1);
				bottomRightCorner = new Vec3d(pos.getX() + 1, pos.getY() + 1 / 16.0f, pos.getZ());
				break;
			default:
				return null;
		}

		//build matrix to rotate
		//GlStateManager.translate(0.5, 0.0, 0.5);
		//GlStateManager.rotate(), 0.0f, 1.0f, 0.0f);
		//GlStateManager.translate(-0.5, 0.0, -0.5);


		final Vec3d u = bottomLeft.subtract(planeOrigin);
		final Vec3d v = bottomRightCorner.subtract(planeOrigin);
		final Vec3d planeNormal = u.crossProduct(v);

		final Vec3d vector3d = CollisionMaths.intersectionLinePlane(headPosition, lookVector, planeOrigin, planeNormal);

		if (vector3d == null) {
			ConductorMod.logger.info("player missed");
			return null;
		} else
		{
			final Vec3d hitVector = vector3d.subtract(pos.getX(), pos.getY(), pos.getZ());

			final Vec3d finalHitVector;
			//FIXME: HAAAAACCCKKKKK
			switch (blockFacing)
			{
				case NORTH:
					finalHitVector = new Vec3d(1-hitVector.x, hitVector.y, 1-hitVector.z);
					break;
				case SOUTH:
					finalHitVector = hitVector;
					break;
				case WEST:
					finalHitVector = new Vec3d(1-hitVector.x, hitVector.y, hitVector.z);
					break;
				case EAST:
					finalHitVector = new Vec3d(hitVector.x, hitVector.y, 1-hitVector.z);
					break;
				default:
					finalHitVector = hitVector;
			}
			ConductorMod.logger.info("player clicked at {},{},{}", finalHitVector.x, finalHitVector.y, finalHitVector.z);
			return finalHitVector;
		}

	}
}
