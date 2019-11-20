package org.atomicworkshop.jammachine.libraries;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary {
    @Nonnull
    public static final Item sequencer;
    @Nonnull
    @ObjectHolder("punchcardblank")
    public static final ItemPunchCardBlank punchCardBlank;
    @Nonnull
    @ObjectHolder("punchcardwritten")
    public static final ItemPunchCardWritten punchCardWritten;

    //Trick IntelliJ/Eclipse into thinking that sequencer won't be null
    static {
        sequencer = null;
        punchCardBlank = null;
        punchCardWritten = null;
    }
}
