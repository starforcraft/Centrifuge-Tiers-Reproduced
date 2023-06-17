package com.ultramega.centrifugetiersreproduced.jei;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.gui.TieredCentrifugeScreen;
import com.ultramega.centrifugetiersreproduced.registry.ModBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static cy.jdkdigital.productivebees.integrations.jei.ProductiveBeesJeiPlugin.CENTRIFUGE_TYPE;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CentrifugeTiersReproduced.MOD_ID, CentrifugeTiersReproduced.MOD_ID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.HIGH_END_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.NUCLEAR_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COSMIC_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CREATIVE_CENTRIFUGE.get()), CENTRIFUGE_TYPE);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 17, 24, 16, CENTRIFUGE_TYPE);
        registration.addRecipeClickArea(TieredCentrifugeScreen.class, 35, 53, 24, 16, CENTRIFUGE_TYPE);
    }
}
