package org.atomicworkshop.jammachine.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import org.atomicworkshop.jammachine.libraries.BlockLibrary;
import org.atomicworkshop.jammachine.libraries.ContainerTypeLibrary;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;

import javax.annotation.Nonnull;

public class SequencerContainer extends Container {
    private final IWorldPosCallable position;

    private final int[] patternState = new int[16 * 25];

    public SequencerContainer(int windowId, PlayerInventory playerInventory, PacketBuffer extraData) {
        this(windowId, playerInventory, IWorldPosCallable.of(playerInventory.player.world, extraData.readBlockPos()));
    }

    public SequencerContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable position) {
        super(ContainerTypeLibrary.sequencer_container, windowId);
        this.position = position;

        for (int interval=0;interval<16;interval++) {
            for (int pitch=0;pitch<25;pitch++) {
                trackInt(IntReferenceHolder.create(patternState, interval * 25 + pitch));
            }
        }
    }



    public boolean set(int pitch, int interval, boolean isSet) {
        final int index = interval * 25 + pitch;
        patternState[index] = isSet ? 1 : 0;
        return patternState[index] == 1;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return isWithinUsableDistance(this.position, playerIn, BlockLibrary.sequencer);
    }

    public SequencerTileEntity getTileEntity() {
        return this.position.apply((world, position) -> {
            TileEntity te = world.getTileEntity(position);
            if (te instanceof SequencerTileEntity) {
                return (SequencerTileEntity)te;
            }
            return null;
        }).get();
    }
}
