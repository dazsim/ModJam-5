package org.atomicworkshop.libraries;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.Reference;
import org.atomicworkshop.blocks.BlockSequencer;
import org.atomicworkshop.blocks.BlockController;

import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary
{
	@Nonnull
	public static final BlockSequencer sequencer;
	@Nonnull
	public static final BlockController synchronizer;

	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
		synchronizer = null;
	}
}
