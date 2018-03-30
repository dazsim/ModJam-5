package org.atomicworkshop.libraries;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.Reference;
import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class BlockLibrary
{
	@Nonnull
	public static final Block sequencer;

	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
	}
}
