package org.atomicworkshop.jammachine.libraries;

import javax.annotation.Nonnull;

import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.blocks.BlockCable;
import org.atomicworkshop.jammachine.blocks.BlockController;
import org.atomicworkshop.jammachine.blocks.BlockSequencer;

import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary
{
	@Nonnull
	public static final BlockSequencer sequencer;
	@Nonnull
	public static final BlockController controller;
	@Nonnull
	public static final BlockCable cable;
	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
		controller = null;
		cable = null;
	}
}
