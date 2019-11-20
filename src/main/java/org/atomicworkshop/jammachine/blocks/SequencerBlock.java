package org.atomicworkshop.jammachine.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import org.atomicworkshop.jammachine.JamMachineMod;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;
import org.atomicworkshop.jammachine.sequencing.MusicPlayer;
import org.atomicworkshop.jammachine.util.CollisionMaths;

import javax.annotation.Nullable;

public class SequencerBlock extends Block {
    public SequencerBlock(Properties properties) {
        super(properties);

        final BlockState defaultState = this.stateContainer.getBaseState()
                .with(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);
        setDefaultState(defaultState);
    }

    @Override
    @Deprecated
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext selectionContext) {
        return VoxelShapes.create(0, 0, 0, 1, 0.5, 1);
    }

    @Override
    public boolean shouldCheckWeakPower(BlockState state, IWorldReader world, BlockPos pos, Direction side) {
        //Disabling weak power checks here prevents Note Blocks from being fired when the sequencer receives a redstone
        //signal.
        return false;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext useContext) {
        return getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, useContext.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SequencerTileEntity();
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof SequencerTileEntity) {
            final SequencerTileEntity teSequencer = (SequencerTileEntity)tileEntity;

            teSequencer.stopPlaying();
            MusicPlayer.stopTrackingSequencerAt(world, pos);
        }

        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity tileEntity, ItemStack itemStack) {
        if (tileEntity instanceof SequencerTileEntity) {
            final SequencerTileEntity teSequencer = (SequencerTileEntity)tileEntity;
            if (teSequencer.getHasCard()) {
                teSequencer.ejectCard(pos.getX(), pos.getY(), pos.getZ());
            }
        }

        super.harvestBlock(worldIn, player, pos, state, tileEntity, itemStack);
    }



    @Override
    public void neighborChanged(BlockState blockState, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        boolean isPowered = false;
        for (final Direction value : Direction.values())
        {
            isPowered = worldIn.isBlockPowered(pos.offset(value));
            if (isPowered) break;
        }

        final SequencerTileEntity tileEntity = getTileEntity(worldIn, pos);
        if (tileEntity == null) return;

        tileEntity.notifyPowered(isPowered);

    }

    @Override
    @Deprecated
    public boolean eventReceived(BlockState blockState, World world, BlockPos pos, int id, int param) {
        final SequencerTileEntity tileEntity = getTileEntity(world, pos);
        if (tileEntity == null) return false;

        return tileEntity.receiveClientEvent(id, param);
    }

    public SequencerTileEntity getTileEntity(World world, BlockPos pos) {
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof SequencerTileEntity) {
            return (SequencerTileEntity)tileEntity;
        }
        JamMachineMod.LOGGER.error("No Tile entity found at block location? {}", pos);
        return null;
    }

    @Override
    public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult rayTraceResult) {
        /*
         * This is where we handle interaction with the Sequencer
         * Insert card on right click with card in hand
         * right click card area in order to remove card
         * right click buttons to toggle
         * right click BPM controls to change BPM
         *
         */
        final SequencerTileEntity teSequencer = getTileEntity(worldIn, pos);
        if (teSequencer == null) {
            return false;
        }

        ItemStack heldItemStack = playerIn.getHeldItemMainhand();
        if (heldItemStack.isEmpty()) {
            heldItemStack = playerIn.getHeldItemOffhand();
        }
        final Item heldItem = heldItemStack.getItem();

        if (heldItemStack.isEmpty()) {
            if (hand == Hand.OFF_HAND) return false;
            final Vec3d hitVec = CollisionMaths.calculateSlopeHit(pos, state.get(BlockStateProperties.HORIZONTAL_FACING), playerIn);
            if (hitVec == null) return false;
            return teSequencer.checkPlayerInteraction(hitVec.x, hitVec.z, playerIn);
        }

        if (heldItem instanceof ItemPunchCardBlank) {
            //you just inserted a blank card into sequencer. load the sequence onto it?
            if (teSequencer.getHasCard()) {
                JamMachineMod.LOGGER.debug("Sequencer already has Card");
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
                JamMachineMod.LOGGER.debug("Sequencer already has Card");
                return false;
            } else {
                teSequencer.loadFromCard(heldItemStack);
                heldItemStack.shrink(1);
                teSequencer.setHasCard(true);
                return true;
            }
        }

        return false;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}
