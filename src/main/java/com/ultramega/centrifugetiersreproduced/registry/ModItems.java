package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, CentrifugeTiersReproduced.MODID);

    public static final DeferredHolder<Item, BlockItem> TIER_1_CENTRIFUGE_CONTROLLER = ITEMS.register("tier_1_centrifuge_controller", () -> new BlockItem(ModBlocks.TIER_1_CENTRIFUGE_CONTROLLER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_2_CENTRIFUGE_CONTROLLER = ITEMS.register("tier_2_centrifuge_controller", () -> new BlockItem(ModBlocks.TIER_2_CENTRIFUGE_CONTROLLER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_3_CENTRIFUGE_CONTROLLER = ITEMS.register("tier_3_centrifuge_controller", () -> new BlockItem(ModBlocks.TIER_3_CENTRIFUGE_CONTROLLER.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_4_CENTRIFUGE_CONTROLLER = ITEMS.register("tier_4_centrifuge_controller", () -> new BlockItem(ModBlocks.TIER_4_CENTRIFUGE_CONTROLLER.get(), new Item.Properties()));

    public static final DeferredHolder<Item, BlockItem> TIER_1_CENTRIFUGE_CASING = ITEMS.register("tier_1_centrifuge_casing", () -> new BlockItem(ModBlocks.TIER_1_CENTRIFUGE_CASING.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_2_CENTRIFUGE_CASING = ITEMS.register("tier_2_centrifuge_casing", () -> new BlockItem(ModBlocks.TIER_2_CENTRIFUGE_CASING.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_3_CENTRIFUGE_CASING = ITEMS.register("tier_3_centrifuge_casing", () -> new BlockItem(ModBlocks.TIER_3_CENTRIFUGE_CASING.get(), new Item.Properties()));
    public static final DeferredHolder<Item, BlockItem> TIER_4_CENTRIFUGE_CASING = ITEMS.register("tier_4_centrifuge_casing", () -> new BlockItem(ModBlocks.TIER_4_CENTRIFUGE_CASING.get(), new Item.Properties()));
}