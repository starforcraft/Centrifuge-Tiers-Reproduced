package com.ultramega.centrifugetiersreproduced.recipe;

import com.ultramega.centrifugetiersreproduced.blockentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;

import java.util.Map;

public class TieredCentrifugeRecipe extends CentrifugeRecipe {
    public TieredCentrifugeRecipe(CentrifugeRecipe recipe) {
        super(recipe.getId(), recipe.ingredient, recipe.itemOutput, recipe.fluidOutput, recipe.getProcessingTime());
    }

    public TieredCentrifugeRecipe(ResourceLocation id, Ingredient ingredient, Map<Ingredient, IntArrayTag> itemOutput, Map<String, Integer> fluidOutput, int processingTime) {
        super(id, ingredient, itemOutput, fluidOutput, processingTime);
    }

    @Override
    public boolean matches(Container inv, Level worldIn) {
        return matches(inv, worldIn, 0);
    }

    public boolean matches(Container inv, Level worldIn, int index) {
        if (this.ingredient.getItems().length > 0) {
            ItemStack invStack = inv.getItem(InventoryHandlerHelper.INPUT_SLOT[index]);

            for (ItemStack stack : this.ingredient.getItems()) {
                if (stack.getItem().equals(invStack.getItem())) {
                    // Check configurable honeycombs
                    if (stack.hasTag() && invStack.hasTag()) {
                        return stack.getTag().equals(invStack.getTag());
                    }
                    return true;
                }
            }
        }
        return false;
    }

}