package org.atomicworkshop.jammachine.libraries;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;

import javax.annotation.Nonnull;

@SuppressWarnings("ConstantConditions")
@ObjectHolder(Reference.MOD_ID)
public class TileEntityTypeLibrary {
    @Nonnull
    public static final TileEntityType<SequencerTileEntity> sequencer_tile;

    static {
        sequencer_tile = null;
    }
}
