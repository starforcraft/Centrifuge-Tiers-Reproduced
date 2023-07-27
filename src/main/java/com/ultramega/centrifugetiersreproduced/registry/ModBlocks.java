package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<Block> HIGH_END_CENTRIFUGE = BLOCKS.register("high_end_centrifuge", () -> new com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeBlock(CentrifugeTiers.HIGH_END, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> NUCLEAR_CENTRIFUGE = BLOCKS.register("nuclear_centrifuge", () -> new com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeBlock(CentrifugeTiers.NUCLEAR, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> COSMIC_CENTRIFUGE = BLOCKS.register("cosmic_centrifuge", () -> new com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeBlock(CentrifugeTiers.COSMIC, Block.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> CREATIVE_CENTRIFUGE = BLOCKS.register("creative_centrifuge", () -> new com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeBlock(CentrifugeTiers.CREATIVE, Block.Properties.copy(Blocks.CAULDRON)));
}