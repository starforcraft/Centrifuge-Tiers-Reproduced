package com.ultramega.centrifugetiersreproduced.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistryHandler {
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeTabs.TABS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModMenuTypes.MENU_TYPES.register(bus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(bus);
	}
}
