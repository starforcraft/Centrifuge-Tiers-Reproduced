package com.ultramega.centrifugetiersreproduced.compat.jei;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.gui.TieredCentrifugeScreen;
import com.ultramega.centrifugetiersreproduced.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import static cy.jdkdigital.productivebees.compat.jei.ProductiveBeesJeiPlugin.CENTRIFUGE_TYPE;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(CentrifugeTiersReproduced.MODID, CentrifugeTiersReproduced.MODID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TIER_1_CENTRIFUGE_CONTROLLER.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TIER_2_CENTRIFUGE_CONTROLLER.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TIER_3_CENTRIFUGE_CONTROLLER.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.TIER_4_CENTRIFUGE_CONTROLLER.get()), CENTRIFUGE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 17, 24, 16, CENTRIFUGE_TYPE);
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 53, 24, 16, CENTRIFUGE_TYPE);
    }
}
