package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeCasingBlock;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeControllerBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, CentrifugeTiersReproduced.MODID);

    public static final DeferredHolder<Block, TieredCentrifugeControllerBlock> TIER_1_CENTRIFUGE_CONTROLLER = BLOCKS.register("tier_1_centrifuge_controller", () -> new TieredCentrifugeControllerBlock(CentrifugeTiers.TIER_1, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeControllerBlock> TIER_2_CENTRIFUGE_CONTROLLER = BLOCKS.register("tier_2_centrifuge_controller", () -> new TieredCentrifugeControllerBlock(CentrifugeTiers.TIER_2, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeControllerBlock> TIER_3_CENTRIFUGE_CONTROLLER = BLOCKS.register("tier_3_centrifuge_controller", () -> new TieredCentrifugeControllerBlock(CentrifugeTiers.TIER_3, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeControllerBlock> TIER_4_CENTRIFUGE_CONTROLLER = BLOCKS.register("tier_4_centrifuge_controller", () -> new TieredCentrifugeControllerBlock(CentrifugeTiers.TIER_4, Block.Properties.ofFullCopy(Blocks.CAULDRON)));

    public static final DeferredHolder<Block, TieredCentrifugeCasingBlock> TIER_1_CENTRIFUGE_CASING = BLOCKS.register("tier_1_centrifuge_casing", () -> new TieredCentrifugeCasingBlock(CentrifugeTiers.TIER_1, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeCasingBlock> TIER_2_CENTRIFUGE_CASING = BLOCKS.register("tier_2_centrifuge_casing", () -> new TieredCentrifugeCasingBlock(CentrifugeTiers.TIER_2, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeCasingBlock> TIER_3_CENTRIFUGE_CASING = BLOCKS.register("tier_3_centrifuge_casing", () -> new TieredCentrifugeCasingBlock(CentrifugeTiers.TIER_3, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, TieredCentrifugeCasingBlock> TIER_4_CENTRIFUGE_CASING = BLOCKS.register("tier_4_centrifuge_casing", () -> new TieredCentrifugeCasingBlock(CentrifugeTiers.TIER_4, Block.Properties.ofFullCopy(Blocks.CAULDRON)));
}