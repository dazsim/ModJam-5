package org.atomicworkshop.jammachine.libraries;

import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.blocks.SequencerBlock;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary {
    @Nonnull
    public static final SequencerBlock sequencer;

    static {
        sequencer = null;
    }
}
