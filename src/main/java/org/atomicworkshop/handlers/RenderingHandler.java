package org.atomicworkshop.handlers;

import net.minecraftforge.fml.relauncher.Side;
import org.atomicworkshop.Reference.Blocks;
import org.atomicworkshop.libraries.ItemLibrary;
import org.atomicworkshop.tesr.TESRBlockSequencer;
import org.atomicworkshop.tiles.TileEntitySequencer;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySequencer.class, new TESRBlockSequencer());
	}
}
