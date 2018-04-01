package org.atomicworkshop.libraries;

import javax.annotation.Nonnull;

import org.atomicworkshop.Reference;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import org.atomicworkshop.items.ItemPunchCardBlank;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	@Nonnull
	public static final ItemBlock sequencer;
	@Nonnull
	public static final ItemBlock wire;
	@Nonnull
	public static final ItemBlock synchronizer;
	@ObjectHolder("punchcardblank")
	public static final ItemPunchCardBlank punchCardBlank;

	public static final Item itemPunchCard;
	//Trick IntelliJ/Eclipse into thinking that sequencer won't be null
	static {
		sequencer = null;
		wire = null;
		synchronizer = null;

		punchCardBlank = null;
		itemPunchCard = null;
	}
}
