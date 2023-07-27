package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>> HIGH_END_CENTRIFUGE = BLOCK_ENTITY_TYPES.register("high_end_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModBlockEntityTypes.HIGH_END_CENTRIFUGE.get(), pos, state), ModBlocks.HIGH_END_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> NUCLEAR_CENTRIFUGE = BLOCK_ENTITY_TYPES.register("nuclear_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModBlockEntityTypes.NUCLEAR_CENTRIFUGE.get(), pos, state), ModBlocks.NUCLEAR_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> COSMIC_CENTRIFUGE = BLOCK_ENTITY_TYPES.register("cosmic_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModBlockEntityTypes.COSMIC_CENTRIFUGE.get(), pos, state), ModBlocks.COSMIC_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CREATIVE_CENTRIFUGE = BLOCK_ENTITY_TYPES.register("creative_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModBlockEntityTypes.CREATIVE_CENTRIFUGE.get(), pos, state), ModBlocks.CREATIVE_CENTRIFUGE.get())
            .build(null));
}
