package com.ultramega.centrifugetiersreproduced.blocks;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import com.ultramega.centrifugetiersreproduced.registry.ModBlockEntityTypes;

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
import org.jetbrains.annotations.NotNull;

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
    public void appendHoverText(@NotNull ItemStack stack, @javax.annotation.Nullable BlockGetter getter, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, getter, components, flag);
        if(Screen.hasShiftDown()) {
            if (tier == CentrifugeTiers.CREATIVE) {
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.creative_centrifuge.tooltip.noEnergy")
                        .withStyle(ChatFormatting.YELLOW));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.creative_centrifuge.tooltip.fasterThanHeated",
                        tier.getSpeed()).withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.moreOutput",
                        tier.getOutputMultiplier()).withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.slots",
                        tier.getInputSlotAmount()).withStyle(ChatFormatting.AQUA));
            }
            else {
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.fasterThanHeated",
                        tier.getSpeed()).withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.moreOutput",
                        tier.getOutputMultiplier()).withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.slots",
                        tier.getInputSlotAmount()).withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable(
                        "block.centrifugetiersreproduced.centrifuge.tooltip.energyCapacity",
                        String.format("%,d", tier.getEnergyCapacity())).withStyle(ChatFormatting.AQUA));
            }

            components.add(Component.translatable(
                    "block.centrifugetiersreproduced.centrifuge.tooltip.fluidCapacity",
                    String.format("%,d", tier.getFluidCapacity())).withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable(
                    "block.centrifugetiersreproduced.centrifuge.tooltip.maxStackSize",
                    String.format("%,d", tier.getItemMaxStackSize())).withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.literal("Press SHIFT for more information")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }
}
