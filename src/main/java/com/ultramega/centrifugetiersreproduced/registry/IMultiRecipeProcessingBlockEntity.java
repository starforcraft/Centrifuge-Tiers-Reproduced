package com.ultramega.centrifugetiersreproduced.registry;

import cy.jdkdigital.productivebees.common.recipe.TimedRecipeInterface;

public interface IMultiRecipeProcessingBlockEntity {
    int[] getRecipeProgress();

    int getProcessingTime(TimedRecipeInterface var1);

    TimedRecipeInterface[] getCurrentRecipes();
}
