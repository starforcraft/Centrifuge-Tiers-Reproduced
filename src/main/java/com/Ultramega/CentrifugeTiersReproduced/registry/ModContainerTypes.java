package com.Ultramega.CentrifugeTiersReproduced.registry;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.Ultramega.CentrifugeTiersReproduced.container.TieredCentrifugeContainer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerTypes {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> HIGH_END_CENTRIFUGE = CONTAINER_TYPES.register("high_end_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModContainerTypes.HIGH_END_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> NUCLEAR_CENTRIFUGE = CONTAINER_TYPES.register("nuclear_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModContainerTypes.NUCLEAR_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> COSMIC_CENTRIFUGE = CONTAINER_TYPES.register("cosmic_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModContainerTypes.COSMIC_CENTRIFUGE.get(), windowId, playerInventory, data)));
    public static final RegistryObject<MenuType<TieredCentrifugeContainer>> CREATIVE_CENTRIFUGE = CONTAINER_TYPES.register("creative_centrifuge", () ->
            IForgeMenuType.create((windowId, playerInventory, data) -> new TieredCentrifugeContainer(ModContainerTypes.CREATIVE_CENTRIFUGE.get(), windowId, playerInventory, data)));
}
