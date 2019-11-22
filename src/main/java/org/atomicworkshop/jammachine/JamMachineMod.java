package org.atomicworkshop.jammachine;

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.atomicworkshop.jammachine.gui.SequencerScreen;
import org.atomicworkshop.jammachine.libraries.ContainerTypeLibrary;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Reference.MOD_ID)
public class JamMachineMod
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static SimpleChannel CHANNEL;

    public JamMachineMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CHANNEL = NetworkRegistry.newSimpleChannel(Reference.CHANNEL_NAME, () -> "1.0", s -> true, s -> true);

        int packetId = 0;
        CHANNEL.registerMessage(++packetId, SequencerSetPitchAtInterval.class,
                SequencerSetPitchAtInterval::toBytes,
                SequencerSetPitchAtInterval::new,
                SequencerSetPitchAtInterval::handle);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ContainerTypeLibrary.sequencer_container, SequencerScreen::new);
    }
}
