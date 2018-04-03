package org.atomicworkshop.jammachine.libraries;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.blocks.BlockSequencer;
import org.atomicworkshop.jammachine.blocks.BlockController;

import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary
{
	@Nonnull
	public static final BlockSequencer sequencer;
	@Nonnull
	public static final BlockController controller;

	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
		controller = null;
	}
}
