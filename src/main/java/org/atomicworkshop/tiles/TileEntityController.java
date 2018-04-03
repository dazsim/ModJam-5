package org.atomicworkshop.tiles;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import org.atomicworkshop.libraries.BlockLibrary;
import org.atomicworkshop.sequencing.Sequencer;
import org.atomicworkshop.sequencing.SequencerSet;
import org.atomicworkshop.util.SpiralIterable;

import java.util.Iterator;
import java.util.UUID;

public class TileEntityController extends TileEntity implements ITickable
{
    private Iterator<BlockPos> sequencerLocator;
    private BlockPos.MutableBlockPos columnBlockPos = new BlockPos.MutableBlockPos();
    private SequencerSet sequencerSet;

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        if (sequencerSet == null) {
            this.sequencerSet = new SequencerSet(world, UUID.randomUUID());
        }
    }

    @Override
    public void update() {
        if (pos == null) return;
        if (world == null) return;

        if ((sequencerLocator == null || !sequencerLocator.hasNext())) {
            sequencerLocator = new SpiralIterable(pos, 1).iterator();
        }

        for (Sequencer sequencer : sequencerSet) {
            if (world.getBlockState(sequencer.getBlockPos()).getBlock() != BlockLibrary.sequencer) {
                sequencerSet.removingSequencer(sequencer);
            }
        }

        BlockPos next = sequencerLocator.next();
        int min = Math.max(pos.getY() - 16, 0);
        int max = Math.min(pos.getY() + 16, world.getActualHeight());

        for (int y = min; y < max; ++y) {
            columnBlockPos.setPos(next.getX(), y, next.getZ());
            Block checkedBlock = world.getBlockState(columnBlockPos).getBlock();
            if (checkedBlock == BlockLibrary.sequencer) {
                TileEntitySequencer teSequencer = BlockLibrary.sequencer.getTileEntity(world, columnBlockPos);
                if (teSequencer != null) {
                    sequencerSet.addSequencer(teSequencer.sequencer);
                }
            }
        }
    }
}
