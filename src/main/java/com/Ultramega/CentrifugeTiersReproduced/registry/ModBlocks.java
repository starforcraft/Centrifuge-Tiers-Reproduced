package com.Ultramega.CentrifugeTiersReproduced.registry;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.Ultramega.CentrifugeTiersReproduced.blocks.TieredCentrifuge;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<Block> HIGH_END_CENTRIFUGE = BLOCKS.register("high_end_centrifuge", () -> new TieredCentrifuge(CentrifugeTiers.HIGH_END, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> NUCLEAR_CENTRIFUGE = BLOCKS.register("nuclear_centrifuge", () -> new TieredCentrifuge(CentrifugeTiers.NUCLEAR, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> COSMIC_CENTRIFUGE = BLOCKS.register("cosmic_centrifuge", () -> new TieredCentrifuge(CentrifugeTiers.COSMIC, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> CREATIVE_CENTRIFUGE = BLOCKS.register("creative_centrifuge", () -> new TieredCentrifuge(CentrifugeTiers.CREATIVE, Block.Properties.copy(Blocks.CAULDRON)));
}