package org.atomicworkshop.jammachine.gui;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import org.atomicworkshop.jammachine.libraries.ContainerTypeLibrary;

import javax.annotation.Nonnull;

public class SequencerContainer extends Container {
    private final IWorldPosCallable position;

    private final int[] patternState = new int[16 * 25];

    public SequencerContainer(int windowId, PlayerInventory playerInventory) {
        this(windowId, playerInventory, IWorldPosCallable.DUMMY);
    }

    public SequencerContainer(int windowId, PlayerInventory playerInventory, IWorldPosCallable position) {
        super(ContainerTypeLibrary.sequencer_container, windowId);
        this.position = position;

        for (int interval=0;interval<16;interval++) {
            for (int pitch=0;pitch<25;pitch++) {
                trackInt(IntReferenceHolder.create(patternState, interval * 16 + pitch));
            }
        }
    }

    public boolean invert(int pitch, int interval) {
        final int index = interval * 16 + pitch;
        patternState[index] = 1-patternState[index];
        return patternState[index] == 1;
    }

    @Override
    public boolean canInteractWith(@Nonnull PlayerEntity playerIn) {
        return isWithinUsableDistance(this.position, playerIn, Blocks.ENCHANTING_TABLE);
    }
}
