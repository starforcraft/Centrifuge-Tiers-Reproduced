package com.ultramega.centrifugetiersreproduced.blocks;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import com.ultramega.centrifugetiersreproduced.registry.ModBlockEntityTypes;
import com.ultramega.centrifugetiersreproduced.config.CentrifugeTiersReproducedConfig;

import cy.jdkdigital.productivebees.common.block.PoweredCentrifuge;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TieredCentrifuge extends PoweredCentrifuge {
    private CentrifugeTiers tier;

    public TieredCentrifuge(CentrifugeTiers tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, getBlockEntity(), (level1, pos1, state1, blockEntity1) -> TieredCentrifugeBlockEntity.tick(level1, pos1, state1, (TieredCentrifugeBlockEntity) blockEntity1));
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TieredCentrifugeBlockEntity(getBlockEntity(), pos, state);
    }

    @Nonnull
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 16, 16);
    }

    private BlockEntityType getBlockEntity() {
        switch(tier) {
            case HIGH_END -> { return ModBlockEntityTypes.HIGH_END_CENTRIFUGE.get(); }
            case NUCLEAR -> { return ModBlockEntityTypes.NUCLEAR_CENTRIFUGE.get(); }
            case COSMIC -> { return ModBlockEntityTypes.COSMIC_CENTRIFUGE.get(); }
            case CREATIVE -> { return ModBlockEntityTypes.CREATIVE_CENTRIFUGE.get(); }
            default -> { return null; }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @javax.annotation.Nullable BlockGetter getter, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, getter, components, flag);
        if(Screen.hasShiftDown()) {
            int outputMultiplier;
            int speedMultiplier;
            int additionalCombSlots;
            int maxStackSize;
            int energyFluidCapacity;

            switch (this.tier) {
                case HIGH_END -> {
                    outputMultiplier = CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_OUTPUT_MULTIPLIER.get();
                    speedMultiplier = CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_SPEED.get();
                    additionalCombSlots = 1;
                    maxStackSize = CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get();
                    energyFluidCapacity = CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_ENERGY_CAPACITY.get();
                }
                case NUCLEAR -> {
                    outputMultiplier = CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_OUTPUT_MULTIPLIER.get();
                    speedMultiplier = CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_SPEED.get();
                    additionalCombSlots = 2;
                    maxStackSize = CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get();
                    energyFluidCapacity = CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_ENERGY_CAPACITY.get();
                }
                case COSMIC -> {
                    outputMultiplier = CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_OUTPUT_MULTIPLIER.get();
                    speedMultiplier = CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_SPEED.get();
                    additionalCombSlots = 3;
                    maxStackSize = CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get();
                    energyFluidCapacity = CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_ENERGY_CAPACITY.get();
                }
                case CREATIVE -> {
                    outputMultiplier = CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_OUTPUT_MULTIPLIER.get();
                    speedMultiplier = 0;
                    additionalCombSlots = 3;
                    maxStackSize = CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get();
                    energyFluidCapacity = CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_FLUID_CAPACITY.get();
                }
                default -> {
                    outputMultiplier = 0;
                    speedMultiplier = 0;
                    additionalCombSlots = 0;
                    maxStackSize = 0;
                    energyFluidCapacity = 0;
                }
            }

            String toolTipText;
            if(tier == CentrifugeTiers.HIGH_END) {
                toolTipText = String.format("%sx the output\n%sx faster than Heated Centrifuge\n%s additional honeycomb slot\nItem Max Stack Size: %s\nEnergy/Fluid Capacity: %s",
                        outputMultiplier, speedMultiplier, additionalCombSlots, maxStackSize, energyFluidCapacity);
            } else if (tier == CentrifugeTiers.CREATIVE) {
                toolTipText = String.format("%sx the output\n1 Tick per Block\n%s additional honeycomb slots\nItem Max Stack Size: %s\nEnergy/Fluid Capacity: %s",
                        outputMultiplier, additionalCombSlots, maxStackSize, energyFluidCapacity);
            } else {
                toolTipText = String.format("%sx the output\n%sx faster than Heated Centrifuge\n%s additional honeycomb slots\nItem Max Stack Size: %s\nEnergy/Fluid Capacity: %s",
                        outputMultiplier, speedMultiplier, additionalCombSlots, maxStackSize, energyFluidCapacity);
            }

            components.add(Component.literal(toolTipText)
                    .withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.literal("Press SHIFT for more information")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }
}
