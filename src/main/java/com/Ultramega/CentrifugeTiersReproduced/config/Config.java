package com.Ultramega.CentrifugeTiersReproduced.config;

import java.io.File;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CentrifugeTiersReproduced.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ForgeConfigSpec.Builder common_builder = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec common_config;
	
	static {
		CentrifugeTiersReproducedConfig.init(common_builder);
		common_config = common_builder.build();
	}
	
	public static void loadConfig(ForgeConfigSpec config, String path) {
		CentrifugeTiersReproduced.LOGGER.info("Loading config: " + path);
		CommentedFileConfig file = CommentedFileConfig.builder(new File(path)).sync().autosave().writingMode(WritingMode.REPLACE).build();
		CentrifugeTiersReproduced.LOGGER.info("Built config: " + path);
		file.load();
		CentrifugeTiersReproduced.LOGGER.info("Loaded config: " + path);
		config.setConfig(file);
	}
}