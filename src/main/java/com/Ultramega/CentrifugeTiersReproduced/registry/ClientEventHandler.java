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
        MenuScreens.register(ModMenuTypes.HIGH_END_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModMenuTypes.NUCLEAR_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModMenuTypes.COSMIC_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
        MenuScreens.register(ModMenuTypes.CREATIVE_CENTRIFUGE.get(), TieredCentrifugeScreen::new);
    }
}
