package org.atomicworkshop.jammachine.libraries;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.gui.SequencerContainer;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
@ObjectHolder(Reference.MOD_ID)
public class ContainerTypeLibrary {
    @Nonnull
    public static final ContainerType<SequencerContainer> sequencer_container;

    static {
        sequencer_container = null;
    }
}
