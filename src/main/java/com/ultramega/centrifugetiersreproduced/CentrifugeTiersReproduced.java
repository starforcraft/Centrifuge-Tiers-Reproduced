package com.ultramega.centrifugetiersreproduced;

import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeCasingBlockEntity;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeControllerBlockEntity;
import com.ultramega.centrifugetiersreproduced.blocks.TieredCentrifugeControllerBlock;
import com.ultramega.centrifugetiersreproduced.config.ServerConfig;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import com.ultramega.centrifugetiersreproduced.gui.TieredCentrifugeScreen;
import com.ultramega.centrifugetiersreproduced.registry.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(CentrifugeTiersReproduced.MODID)
public class CentrifugeTiersReproduced {
    public static final String MODID = "centrifugetiersreproduced";

    public CentrifugeTiersReproduced(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModBlockEntityTypes.BLOCK_ENTITY_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerBlockEntityCapabilities);
        modEventBus.addListener(this::registerScreens);

        modContainer.registerConfig(ModConfig.Type.COMMON, ServerConfig.SPEC);
    }

    private void registerBlockEntityCapabilities(final RegisterCapabilitiesEvent event) {
        for (CentrifugeTiers tier : CentrifugeTiers.values()) {
            event.registerBlockEntity(
                    Capabilities.EnergyStorage.BLOCK,
                    CentrifugeTiersReproduced.getControllerBlockEntityType(tier),
                    (blockEntity, side) -> blockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? blockEntity.energyHandler : null
            );
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    CentrifugeTiersReproduced.getControllerBlockEntityType(tier),
                    (blockEntity, side) -> blockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? blockEntity.inventoryHandler : null
            );
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    CentrifugeTiersReproduced.getControllerBlockEntityType(tier),
                    (blockEntity, side) -> blockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? blockEntity.fluidHandler : null
            );

            event.registerBlockEntity(
                    Capabilities.EnergyStorage.BLOCK,
                    CentrifugeTiersReproduced.getCasingBlockEntityType(tier),
                    (blockEntity, side) -> {
                        TieredCentrifugeControllerBlockEntity controllerBlockEntity = blockEntity.getController();
                        return controllerBlockEntity != null ? (controllerBlockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? controllerBlockEntity.energyHandler : null) : null;
                    }
            );
            event.registerBlockEntity(
                    Capabilities.ItemHandler.BLOCK,
                    CentrifugeTiersReproduced.getCasingBlockEntityType(tier),
                    (blockEntity, side) -> {
                        TieredCentrifugeControllerBlockEntity controllerBlockEntity = blockEntity.getController();
                        return controllerBlockEntity != null ? (controllerBlockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? controllerBlockEntity.inventoryHandler : null) : null;
                    }
            );
            event.registerBlockEntity(
                    Capabilities.FluidHandler.BLOCK,
                    CentrifugeTiersReproduced.getCasingBlockEntityType(tier),
                    (blockEntity, side)  -> {
                        TieredCentrifugeControllerBlockEntity controllerBlockEntity = blockEntity.getController();
                        return controllerBlockEntity != null ? (controllerBlockEntity.getBlockState().getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) ? controllerBlockEntity.fluidHandler : null) : null;
                    }
            );
        }
    }

    private void registerScreens(final RegisterMenuScreensEvent event) {
        event.<TieredCentrifugeContainer, TieredCentrifugeScreen>register(ModMenuTypes.TIER_1_CENTRIFUGE_CONTROLLER_CONTAINER.get(), (container, inventory, component) -> new TieredCentrifugeScreen(CentrifugeTiers.TIER_1, container, inventory, component));
        event.<TieredCentrifugeContainer, TieredCentrifugeScreen>register(ModMenuTypes.TIER_2_CENTRIFUGE_CONTROLLER_CONTAINER.get(), (container, inventory, component) -> new TieredCentrifugeScreen(CentrifugeTiers.TIER_2, container, inventory, component));
        event.<TieredCentrifugeContainer, TieredCentrifugeScreen>register(ModMenuTypes.TIER_3_CENTRIFUGE_CONTROLLER_CONTAINER.get(), (container, inventory, component) -> new TieredCentrifugeScreen(CentrifugeTiers.TIER_3, container, inventory, component));
        event.<TieredCentrifugeContainer, TieredCentrifugeScreen>register(ModMenuTypes.TIER_4_CENTRIFUGE_CONTROLLER_CONTAINER.get(), (container, inventory, component) -> new TieredCentrifugeScreen(CentrifugeTiers.TIER_4, container, inventory, component));
    }

    public static BlockEntityType<TieredCentrifugeCasingBlockEntity> getCasingBlockEntityType(CentrifugeTiers tier) {
        return switch (tier) {
            case TIER_1 -> ModBlockEntityTypes.TIER_1_CENTRIFUGE_CASING_BLOCK_ENTITY.get();
            case TIER_2 -> ModBlockEntityTypes.TIER_2_CENTRIFUGE_CASING_BLOCK_ENTITY.get();
            case TIER_3 -> ModBlockEntityTypes.TIER_3_CENTRIFUGE_CASING_BLOCK_ENTITY.get();
            case TIER_4 -> ModBlockEntityTypes.TIER_4_CENTRIFUGE_CASING_BLOCK_ENTITY.get();
        };
    }

    public static BlockEntityType<TieredCentrifugeControllerBlockEntity> getControllerBlockEntityType(CentrifugeTiers tier) {
        return switch (tier) {
            case TIER_1 -> ModBlockEntityTypes.TIER_1_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY.get();
            case TIER_2 -> ModBlockEntityTypes.TIER_2_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY.get();
            case TIER_3 -> ModBlockEntityTypes.TIER_3_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY.get();
            case TIER_4 -> ModBlockEntityTypes.TIER_4_CENTRIFUGE_CONTROLLER_BLOCK_ENTITY.get();
        };
    }

    public static MenuType<TieredCentrifugeContainer> getMenuType(CentrifugeTiers tier) {
        return switch (tier) {
            case TIER_1 -> ModMenuTypes.TIER_1_CENTRIFUGE_CONTROLLER_CONTAINER.get();
            case TIER_2 -> ModMenuTypes.TIER_2_CENTRIFUGE_CONTROLLER_CONTAINER.get();
            case TIER_3 -> ModMenuTypes.TIER_3_CENTRIFUGE_CONTROLLER_CONTAINER.get();
            case TIER_4 -> ModMenuTypes.TIER_4_CENTRIFUGE_CONTROLLER_CONTAINER.get();
        };
    }
}
