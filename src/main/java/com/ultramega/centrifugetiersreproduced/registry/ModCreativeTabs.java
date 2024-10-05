package com.ultramega.centrifugetiersreproduced.registry;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CentrifugeTiersReproduced.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CENTRIFUGE_TIERS_REPRODUCED_TAB = CREATIVE_MODE_TABS.register(CentrifugeTiersReproduced.MODID + "_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + CentrifugeTiersReproduced.MODID))
            .icon(() -> ModItems.TIER_1_CENTRIFUGE_CONTROLLER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for(DeferredHolder<Item, ? extends Item> item : ModItems.ITEMS.getEntries()) {
                    output.accept(item.get());
                }
            }).build());
}
