package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeCasingBlockEntity;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeControllerBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, CentrifugeTiersReproduced.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeControllerBlockEntity>> TIER_1_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_1_centrifuge_controller", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeControllerBlockEntity(CentrifugeTiers.TIER_1, blockPos, blockState), ModBlocks.TIER_1_CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeControllerBlockEntity>> TIER_2_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_2_centrifuge_controller", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeControllerBlockEntity(CentrifugeTiers.TIER_2, blockPos, blockState), ModBlocks.TIER_2_CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeControllerBlockEntity>> TIER_3_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_3_centrifuge_controller", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeControllerBlockEntity(CentrifugeTiers.TIER_3, blockPos, blockState), ModBlocks.TIER_3_CENTRIFUGE_CONTROLLER.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeControllerBlockEntity>> TIER_4_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_4_centrifuge_controller", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeControllerBlockEntity(CentrifugeTiers.TIER_4, blockPos, blockState), ModBlocks.TIER_4_CENTRIFUGE_CONTROLLER.get())
            .build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeCasingBlockEntity>> TIER_1_CENTRIFUGE_CASING_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_1_centrifuge_casing", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeCasingBlockEntity(CentrifugeTiers.TIER_1, blockPos, blockState), ModBlocks.TIER_1_CENTRIFUGE_CASING.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeCasingBlockEntity>> TIER_2_CENTRIFUGE_CASING_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_2_centrifuge_casing", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeCasingBlockEntity(CentrifugeTiers.TIER_2, blockPos, blockState), ModBlocks.TIER_2_CENTRIFUGE_CASING.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeCasingBlockEntity>> TIER_3_CENTRIFUGE_CASING_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_3_centrifuge_casing", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeCasingBlockEntity(CentrifugeTiers.TIER_3, blockPos, blockState), ModBlocks.TIER_3_CENTRIFUGE_CASING.get())
            .build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredCentrifugeCasingBlockEntity>> TIER_4_CENTRIFUGE_CASING_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register("tier_4_centrifuge_casing", () -> BlockEntityType.Builder
            .of((blockPos, blockState) -> new TieredCentrifugeCasingBlockEntity(CentrifugeTiers.TIER_4, blockPos, blockState), ModBlocks.TIER_4_CENTRIFUGE_CASING.get())
            .build(null));
}
