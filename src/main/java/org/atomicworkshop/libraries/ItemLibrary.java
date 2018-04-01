package org.atomicworkshop.libraries;

import javax.annotation.Nonnull;

import org.atomicworkshop.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	public class itemPunchCardBlank {

	}
	@Nonnull
	public static final ItemBlock sequencer;
	public static final Item itemPunchCardBlank;
	public static final Item itemPunchCard;
	//Trick IntelliJ/Eclipse into thinking that sequencer won't be null
	static {
		sequencer = null;
		itemPunchCardBlank = null;
		itemPunchCard = null;
	}
}
