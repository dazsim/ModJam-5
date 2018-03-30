package org.atomicworkshop.handlers;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import org.atomicworkshop.Reference;
import org.atomicworkshop.Reference.Blocks;
import org.atomicworkshop.blocks.BlockSequencer;
import org.atomicworkshop.libraries.BlockLibrary;

@EventBusSubscriber
public final class RegistrationHandler
{
	@SubscribeEvent
	public static void onRegisterBlocks(Register<Block> registryEvent) {
		final IForgeRegistry<Block> registry = registryEvent.getRegistry();

		registerBlock(registry, new BlockSequencer(), Blocks.sequencer);
	}

	@SubscribeEvent
	public static void onRegisterItems(Register<Item> registryEvent) {
		final IForgeRegistry<Item> registry = registryEvent.getRegistry();

		registerItemFromBlock(registry, BlockLibrary.sequencer);
	}

	private static void registerBlock(IForgeRegistry<Block> registry, Block block, ResourceLocation registryName)
	{
		registry.register(block
				.setRegistryName(registryName)
				.setCreativeTab(Reference.CreativeTab)
				.setUnlocalizedName(registryName.toString())
		);
	}

	private static void registerItemFromBlock(IForgeRegistry<Item> registry, Block block)
	{
		final ResourceLocation registryName = block.getRegistryName();
		assert registryName != null;

		registry.register(new ItemBlock(block)
			.setRegistryName(registryName)
			.setUnlocalizedName(registryName.toString()));
	}
}
