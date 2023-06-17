package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CentrifugeTiersReproduced.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB_CENTRIFUGETIERSREPRODUCED = TABS.register("centrifugetiersreproduced", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.centrifugetiersreproduced")).icon(() -> new ItemStack(ModItems.HIGH_END_CENTRIFUGE.get())).withTabsBefore(CreativeModeTabs.SPAWN_EGGS).displayItems((featureFlags, output) -> {
        output.accept(new ItemStack(ModItems.HIGH_END_CENTRIFUGE.get()));
        output.accept(new ItemStack(ModItems.NUCLEAR_CENTRIFUGE.get()));
        output.accept(new ItemStack(ModItems.COSMIC_CENTRIFUGE.get()));
        output.accept(new ItemStack(ModItems.CREATIVE_CENTRIFUGE.get()));
    }).build());
}
