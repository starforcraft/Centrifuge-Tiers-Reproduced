package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<Item> HIGH_END_CENTRIFUGE = ITEMS.register("high_end_centrifuge", () -> new BlockItem(ModBlocks.HIGH_END_CENTRIFUGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> NUCLEAR_CENTRIFUGE = ITEMS.register("nuclear_centrifuge", () -> new BlockItem(ModBlocks.NUCLEAR_CENTRIFUGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> COSMIC_CENTRIFUGE = ITEMS.register("cosmic_centrifuge", () -> new BlockItem(ModBlocks.COSMIC_CENTRIFUGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CREATIVE_CENTRIFUGE = ITEMS.register("creative_centrifuge", () -> new BlockItem(ModBlocks.CREATIVE_CENTRIFUGE.get(), new Item.Properties()));
}