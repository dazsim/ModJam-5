package org.atomicworkshop.jammachine.handlers;

import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.Reference.Blocks;
import org.atomicworkshop.jammachine.Reference.Items;
import org.atomicworkshop.jammachine.Reference.TileEntities;
import org.atomicworkshop.jammachine.blocks.BlockController;
import org.atomicworkshop.jammachine.blocks.BlockSequencer;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;
import org.atomicworkshop.jammachine.items.ItemBraidedString;
import org.atomicworkshop.jammachine.libraries.BlockLibrary;
import org.atomicworkshop.jammachine.tiles.TileEntityController;
import org.atomicworkshop.jammachine.tiles.TileEntitySequencer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("SameParameterValue")
@EventBusSubscriber
public final class RegistrationHandler
{
	@SubscribeEvent
	public static void onRegisterBlocks(Register<Block> registryEvent) {
		final IForgeRegistry<Block> registry = registryEvent.getRegistry();

		registerBlock(registry, new BlockSequencer(), Blocks.sequencer);
		registerBlock(registry, new BlockController(), Blocks.controller);

		GameRegistry.registerTileEntity(TileEntitySequencer.class, TileEntities.sequencer);
		GameRegistry.registerTileEntity(TileEntityController.class, TileEntities.controller);
		
	}

	@SubscribeEvent
	public static void onRegisterItems(Register<Item> registryEvent) {
		final IForgeRegistry<Item> registry = registryEvent.getRegistry();

		registerItemFromBlock(registry, BlockLibrary.sequencer);
		registerItemFromBlock(registry, BlockLibrary.controller);


		registerItem(registry, new ItemPunchCardBlank(), Items.punchcardblank, true);
		registerItem(registry, new ItemPunchCardWritten(), Items.punchcardwritten, false);
		registerItem(registry, new ItemBraidedString(), Items.braidedstring, true);
		
	}

	private static void registerBlock(IForgeRegistry<Block> registry, Block block, ResourceLocation registryName)
	{
		registry.register(block
				.setRegistryName(registryName)
				.setCreativeTab(Reference.CreativeTab)
				.setUnlocalizedName(registryName.toString())
		);
	}

	private static void registerItem(IForgeRegistry<Item> registry, Item item, ResourceLocation registryName, boolean showInCreativeTab)
	{
		item.setRegistryName(registryName)
			.setUnlocalizedName(registryName.toString());

		if (showInCreativeTab) {
			item.setCreativeTab(Reference.CreativeTab);
		}

		registry.register(item);
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
