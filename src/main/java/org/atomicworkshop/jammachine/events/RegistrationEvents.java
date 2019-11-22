package org.atomicworkshop.jammachine.events;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.network.IContainerFactory;
import org.atomicworkshop.jammachine.gui.SequencerContainer;
import org.atomicworkshop.jammachine.sequencing.Sequencer;
import org.atomicworkshop.jammachine.tiles.SequencerTileEntity;
import org.atomicworkshop.jammachine.items.ItemPunchCardBlank;
import org.atomicworkshop.jammachine.items.ItemPunchCardWritten;
import org.atomicworkshop.jammachine.libraries.BlockLibrary;
import org.atomicworkshop.jammachine.libraries.ItemLibrary;
import org.atomicworkshop.jammachine.Reference;
import org.atomicworkshop.jammachine.blocks.SequencerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistrationEvents {
    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> registryEvent) {
        final IForgeRegistry<Block> registry = registryEvent.getRegistry();

        Block.Properties machineProperties = Block.Properties
                .create(Material.WOOD)
                .harvestTool(ToolType.AXE);

        registry.register(new SequencerBlock(machineProperties).setRegistryName(Reference.Blocks.SEQUENCER));
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> registryEvent) {
        final IForgeRegistry<Item> registry = registryEvent.getRegistry();

        ItemGroup itemGroup = new ItemGroup("itemgroup.jammachine") {
            ItemStack _itemStack;

            @Override
            @Nonnull
            public ItemStack createIcon() {

                if (_itemStack == null) {
                    _itemStack = new ItemStack(ItemLibrary.sequencer);
                }
                return _itemStack;
            }
        };

        registry.register(new BlockItem(BlockLibrary.sequencer, new Item.Properties().group(itemGroup)).setRegistryName(Reference.Blocks.SEQUENCER));
        registry.register(new ItemPunchCardBlank(new Item.Properties().group(itemGroup)).setRegistryName(Reference.Items.PUNCH_CARD_BLANK));
        registry.register(new ItemPunchCardWritten(new Item.Properties()).setRegistryName(Reference.Items.PUNCH_CARD_WRITTEN));
    }

    @SubscribeEvent
    public static void onRegisterTileEntityTypes(RegistryEvent.Register<TileEntityType<?>> registryEvent) {
        final IForgeRegistry<TileEntityType<?>> registry = registryEvent.getRegistry();

        registry.register(
                TileEntityType.Builder
                        .create(SequencerTileEntity::new, BlockLibrary.sequencer)
                        .build(null)
                        .setRegistryName(Reference.TileEntities.SEQUENCER)
        );
    }

    @SubscribeEvent
    public static void onRegisterContainerTypes(RegistryEvent.Register<ContainerType<?>> registryEvent) {
        final IForgeRegistry<ContainerType<?>> registry = registryEvent.getRegistry();
        registry.register(new ContainerType<>((IContainerFactory<SequencerContainer>)SequencerContainer::new).setRegistryName(Reference.Container.SEQUENCER));
    }
}
