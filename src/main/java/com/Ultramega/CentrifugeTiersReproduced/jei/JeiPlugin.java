package com.Ultramega.CentrifugeTiersReproduced.jei;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiersReproduced;
import com.Ultramega.CentrifugeTiersReproduced.gui.TieredCentrifugeScreen;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static cy.jdkdigital.productivebees.integrations.jei.ProductiveBeesJeiPlugin.CATEGORY_CENTRIFUGE_UID;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CentrifugeTiersReproduced.MOD_ID, CentrifugeTiersReproduced.MOD_ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HIGH_END_CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NUCLEAR_CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COSMIC_CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CREATIVE_CENTRIFUGE.get()), CATEGORY_CENTRIFUGE_UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 17, 24, 16, CATEGORY_CENTRIFUGE_UID);
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 53, 24, 16, CATEGORY_CENTRIFUGE_UID);
    }
}
