package org.atomicworkshop.libraries;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.Reference;
import javax.annotation.Nonnull;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	@Nonnull
	public static final ItemBlock sequencer;

	//Trick IntelliJ into thinking that sequencer won't be null
	static {
		sequencer = null;
	}
}
