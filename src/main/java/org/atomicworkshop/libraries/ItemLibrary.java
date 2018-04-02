package org.atomicworkshop.libraries;

import javax.annotation.Nonnull;

import org.atomicworkshop.Reference;
import org.atomicworkshop.items.ItemPunchCardBlank;
import org.atomicworkshop.items.ItemPunchCardWritten;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@SuppressWarnings("ALL")
@ObjectHolder(Reference.MOD_ID)
public class ItemLibrary
{
	@Nonnull
	public static final ItemBlock sequencer;

	@ObjectHolder("punchcardblank")
	@Nonnull
	public static final ItemPunchCardBlank punchCardBlank;

	@ObjectHolder("punchcardwritten")
	@Nonnull
	public static final ItemPunchCardWritten punchCardWritten;

	//Trick IntelliJ/Eclipse into thinking that sequencer won't be null
	static {
		sequencer = null;
		punchCardBlank = null;
		punchCardWritten = null;
	}
}
