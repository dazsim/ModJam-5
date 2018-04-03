package org.atomicworkshop.jammachine.handlers;

import org.atomicworkshop.jammachine.Reference.Blocks;
import org.atomicworkshop.jammachine.Reference.Items;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;
import org.atomicworkshop.jammachine.tesr.TESRBlockController;
import org.atomicworkshop.jammachine.tesr.TESRBlockSequencer;
import org.atomicworkshop.jammachine.tiles.TileEntityController;
import org.atomicworkshop.jammachine.tiles.TileEntitySequencer;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class RenderingHandler
{
	@SubscribeEvent
	public static void onRenderingReady(ModelRegistryEvent evt)
	{
		ModelLoader.setCustomModelResourceLocation(
				ItemLibrary.sequencer,
				0,
				new ModelResourceLocation(Blocks.sequencer, "inventory")
		);
		//ModelLoader.setCustomModelResourceLocation(
		//		ItemLibrary.controller,
		//		0,
		//		new ModelResourceLocation(Blocks.controller, "inventory")
		//);
		ModelLoader.setCustomModelResourceLocation(
				ItemLibrary.punchCardBlank,
				0,
				new ModelResourceLocation(Items.punchcardblank, "inventory")
		);
		ModelLoader.setCustomModelResourceLocation(
				ItemLibrary.punchCardWritten,
				0,
				new ModelResourceLocation(Items.punchcardwritten, "inventory")
		);
		
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySequencer.class, new TESRBlockSequencer());
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityController.class, new TESRBlockController());
	}
}
