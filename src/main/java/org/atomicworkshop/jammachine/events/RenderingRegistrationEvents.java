package org.atomicworkshop.jammachine.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;
import org.atomicworkshop.jammachine.ter.SequencerTER;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RenderingRegistrationEvents {
    @SubscribeEvent
    public static void onRenderingReady(ModelRegistryEvent event) {
        ClientRegistry.bindTileEntitySpecialRenderer(SequencerTileEntity.class, new SequencerTER());
    }
}
