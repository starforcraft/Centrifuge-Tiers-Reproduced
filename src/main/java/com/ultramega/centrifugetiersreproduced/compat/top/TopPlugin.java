package com.ultramega.centrifugetiersreproduced.compat.top;

import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import com.ultramega.centrifugetiersreproduced.registry.IMultiRecipeProcessingBlockEntity;
import mcjty.theoneprobe.api.CompoundText;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.TextStyleClass;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.function.Function;

public class TopPlugin implements Function<ITheOneProbe, Void> {
    @Nullable
    @Override
    public Void apply(ITheOneProbe theOneProbe) {
        theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
            BlockEntity tileEntity = world.getBlockEntity(data.getPos());

            if (tileEntity instanceof IMultiRecipeProcessingBlockEntity recipeProcessingBlockEntity) {
                int inputAmount = ((TieredCentrifugeBlockEntity)tileEntity).tier.getInputSlotAmount();

                probeInfo.horizontal()
                        .item(new ItemStack(blockState.getBlock().asItem()))
                        .vertical()
                        .itemLabel(new ItemStack(blockState.getBlock().asItem()));

                for(int i = 0; i < inputAmount; i++) {
                    if (recipeProcessingBlockEntity.getRecipeProgress()[i] > 0) {
                        probeInfo.progress(recipeProcessingBlockEntity.getRecipeProgress()[i], recipeProcessingBlockEntity.getProcessingTime(recipeProcessingBlockEntity.getCurrentRecipes()[i]));
                    }
                }

                probeInfo.text(CompoundText.create().style(TextStyleClass.MODNAME).text("Centrifuge Tiers Reproduced"));

                return true;
            }
            return false;
        });

        return null;
    }
}
