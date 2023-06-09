package com.Ultramega.CentrifugeTiersReproduced.registry;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.Ultramega.CentrifugeTiersReproduced.blockentity.TieredCentrifugeBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntityTypes {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<BlockEntityType<?>> HIGH_END_CENTRIFUGE = BLOCK_ENTITIES.register("high_end_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModTileEntityTypes.HIGH_END_CENTRIFUGE.get(), pos, state), ModBlocks.HIGH_END_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> NUCLEAR_CENTRIFUGE = BLOCK_ENTITIES.register("nuclear_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModTileEntityTypes.NUCLEAR_CENTRIFUGE.get(), pos, state), ModBlocks.NUCLEAR_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> COSMIC_CENTRIFUGE = BLOCK_ENTITIES.register("cosmic_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModTileEntityTypes.COSMIC_CENTRIFUGE.get(), pos, state), ModBlocks.COSMIC_CENTRIFUGE.get())
            .build(null));
    public static final RegistryObject<BlockEntityType<?>> CREATIVE_CENTRIFUGE = BLOCK_ENTITIES.register("creative_centrifuge", () -> BlockEntityType.Builder
            .of((pos, state) -> new TieredCentrifugeBlockEntity(ModTileEntityTypes.CREATIVE_CENTRIFUGE.get(), pos, state), ModBlocks.CREATIVE_CENTRIFUGE.get())
            .build(null));
}
