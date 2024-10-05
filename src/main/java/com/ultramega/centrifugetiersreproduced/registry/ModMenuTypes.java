package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.container.TieredCentrifugeContainer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(BuiltInRegistries.MENU, CentrifugeTiersReproduced.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<TieredCentrifugeContainer>> TIER_1_CENTRIFUGE_CONTROLLER_CONTAINER = MENU_TYPES.register("tier_1_centrifuge_controller", () ->
            IMenuTypeExtension.create((containerId, inventory, buf) -> new TieredCentrifugeContainer(CentrifugeTiers.TIER_1, containerId, inventory, buf))
    );
    public static final DeferredHolder<MenuType<?>, MenuType<TieredCentrifugeContainer>> TIER_2_CENTRIFUGE_CONTROLLER_CONTAINER = MENU_TYPES.register("tier_2_centrifuge_controller", () ->
            IMenuTypeExtension.create((containerId, inventory, buf) -> new TieredCentrifugeContainer(CentrifugeTiers.TIER_2, containerId, inventory, buf))
    );
    public static final DeferredHolder<MenuType<?>, MenuType<TieredCentrifugeContainer>> TIER_3_CENTRIFUGE_CONTROLLER_CONTAINER = MENU_TYPES.register("tier_3_centrifuge_controller", () ->
            IMenuTypeExtension.create((containerId, inventory, buf) -> new TieredCentrifugeContainer(CentrifugeTiers.TIER_3, containerId, inventory, buf))
    );
    public static final DeferredHolder<MenuType<?>, MenuType<TieredCentrifugeContainer>> TIER_4_CENTRIFUGE_CONTROLLER_CONTAINER = MENU_TYPES.register("tier_4_centrifuge_controller", () ->
            IMenuTypeExtension.create((containerId, inventory, buf) -> new TieredCentrifugeContainer(CentrifugeTiers.TIER_4, containerId, inventory, buf))
    );
}
