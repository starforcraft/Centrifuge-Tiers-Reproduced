package com.ultramega.centrifugetiersreproduced.recipe;

import com.ultramega.centrifugetiersreproduced.blockentity.InventoryHandlerHelper;
import cy.jdkdigital.productivebees.common.recipe.CentrifugeRecipe;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TieredCentrifugeRecipe extends CentrifugeRecipe {
    public TieredCentrifugeRecipe(CentrifugeRecipe recipe) {
        super(recipe.getId(), recipe.ingredient, recipe.itemOutput, recipe.fluidOutput, recipe.getProcessingTime());
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