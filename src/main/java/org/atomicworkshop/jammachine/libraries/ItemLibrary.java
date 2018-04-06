package org.atomicworkshop.jammachine.libraries;

import javax.annotation.Nonnull;

import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.items.ItemBraidedString;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;

import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	@Nonnull
	public static final ItemBlock sequencer;

	@Nonnull
	public static final ItemBlock controller;

	@ObjectHolder("punchcardblank")
	@Nonnull
	public static final ItemPunchCardBlank punchCardBlank;

	@ObjectHolder("punchcardwritten")
	@Nonnull
	public static final ItemPunchCardWritten punchCardWritten;

	@ObjectHolder("braidedstring")
	@Nonnull
	public static final ItemBraidedString braidedString;
	
	//Trick IntelliJ/Eclipse into thinking that sequencer won't be null
	static {
		sequencer = null;
		controller = null;

		punchCardBlank = null;
		punchCardWritten = null;
		braidedString = null;
	}
}
