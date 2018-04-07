package org.atomicworkshop.jammachine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
public class BlockSequencer extends BlockHorizontal implements ITileEntityProvider
{
	private final AxisAlignedBB boundingBox = new AxisAlignedBB(0, 0, 0, 1, 0.5, 1);

	public BlockSequencer() {
		super(new MachineMaterial());

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
		return boundingBox;
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
			if (teSequencer.getHasCard())
			{
				teSequencer.ejectCard(pos.getX(), pos.getY(), pos.getZ());
			}

			teSequencer.stopPlaying();
			MusicPlayer.stopTrackingSequencerAt(worldIn, pos);
		}

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
		 * This is where we handle interaction with the Sequencer
		 * Insert card on right click with card in hand
		 * right click card area in order to remove card
		 * right click buttons to toggle
		 * right click BPM controls to change BPM
		 * 
		 */
	    JamMachineMod.logger.info("Sequencer Clicked");
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
			final Vec3d hitVec = CollisionMaths.calculateSlopeHit(pos, state.getValue(FACING), playerIn);
			if (hitVec == null) return false;
			return teSequencer.checkPlayerInteraction(hitVec.x, hitVec.z, playerIn); 
		}

	    if (heldItem instanceof ItemPunchCardBlank) {
		    //you just inserted a blank card into sequencer. load the sequence onto it?
		    if (teSequencer.getHasCard()) {
			    JamMachineMod.logger.debug("Sequencer already has Card");
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
			    JamMachineMod.logger.debug("Sequencer already has Card");
			    return false;
		    } else {
		    	teSequencer.loadFromCard(heldItemStack);
		    	heldItemStack.shrink(1);
		    	teSequencer.setHasCard(true);
		    	return true;
		    }
	    }

	    if (heldItem instanceof ItemNameTag) {
			//Naming Sequencer
			NBTTagCompound tagCompound = heldItemStack.getTagCompound();
			//Not using reference because these are vanilla NBT tags.
			NBTTagCompound display = tagCompound.getCompoundTag("display");
			teSequencer.setCustomName(display.getString("Name"));
			return true;
		}

	    return false;
    }


}
