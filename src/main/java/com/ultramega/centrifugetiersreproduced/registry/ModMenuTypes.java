package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> HIGH_END_CENTRIFUGE = MENU_TYPES.register("high_end_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModMenuTypes.HIGH_END_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> NUCLEAR_CENTRIFUGE = MENU_TYPES.register("nuclear_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModMenuTypes.NUCLEAR_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> COSMIC_CENTRIFUGE = MENU_TYPES.register("cosmic_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModMenuTypes.COSMIC_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> CREATIVE_CENTRIFUGE = MENU_TYPES.register("creative_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModMenuTypes.CREATIVE_CENTRIFUGE.get(), windowId, playerInventory, data)));
}
