package com.Ultramega.CentrifugeTiersReproduced;

import com.Ultramega.CentrifugeTiersReproduced.config.Config;
import com.Ultramega.CentrifugeTiersReproduced.registry.ClientEventHandler;
import com.Ultramega.CentrifugeTiersReproduced.registry.RegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CentrifugeTiersReproduced.MOD_ID)
public class CentrifugeTiersReproduced {
    public static final String MOD_ID = "centrifugetiersreproduced";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public CentrifugeTiersReproduced() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientEventHandler::clientStuff);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.common_config);
        Config.loadConfig(Config.common_config, FMLPaths.CONFIGDIR.get().resolve("centrifugetiersreproduced-common.toml").toString());

        RegistryHandler.init();
    }
}
