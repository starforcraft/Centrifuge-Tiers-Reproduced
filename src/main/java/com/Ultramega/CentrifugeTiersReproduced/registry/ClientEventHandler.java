package com.Ultramega.CentrifugeTiersReproduced.registry;

import com.Ultramega.CentrifugeTiersReproduced.gui.TieredCentrifugeScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ClientEventHandler {
    public static void clientStuff() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientEventHandler::doClientStuff);
    }

    private static void doClientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(ModContainerTypes.HIGH_END_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModContainerTypes.NUCLEAR_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModContainerTypes.COSMIC_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModContainerTypes.CREATIVE_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
    }
}
