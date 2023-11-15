package com.ultramega.centrifugetiersreproduced;

import com.ultramega.centrifugetiersreproduced.compat.top.TopPlugin;
import com.ultramega.centrifugetiersreproduced.config.Config;
import com.ultramega.centrifugetiersreproduced.registry.ClientEventHandler;
import com.ultramega.centrifugetiersreproduced.registry.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CentrifugeTiersReproduced.MOD_ID)
public class CentrifugeTiersReproduced {
    public static final String MOD_ID = "centrifugetiersreproduced";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CentrifugeTiersReproduced() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::clientStuff);

        RegistryHandler.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInterModEnqueue);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
        Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve(CentrifugeTiersReproduced.MOD_ID + "-common.toml").toString());
    }

    public void onInterModEnqueue(InterModEnqueueEvent event) {
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", TopPlugin::new);
    }
}
