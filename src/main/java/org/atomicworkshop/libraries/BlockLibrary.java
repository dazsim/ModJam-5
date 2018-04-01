package org.atomicworkshop.libraries;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.Reference;
import org.atomicworkshop.blocks.BlockSequencer;
import org.atomicworkshop.blocks.BlockSynchronizer;
import org.atomicworkshop.blocks.BlockWire;
import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary
{
	@Nonnull
	public static final BlockSequencer sequencer;
	@Nonnull
	public static final BlockWire wire;
	@Nonnull
	public static final BlockSynchronizer synchronizer;

	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
		wire = null;
		synchronizer = null;
	}
}
