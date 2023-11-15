package com.ultramega.centrifugetiersreproduced.blocks;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeBlockEntity;
import com.ultramega.centrifugetiersreproduced.registry.ModBlockEntityTypes;
import cy.jdkdigital.productivebees.common.block.Centrifuge;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TieredCentrifugeBlock extends Centrifuge {
    private final CentrifugeTiers tier;

    public TieredCentrifugeBlock(CentrifugeTiers tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Nonnull
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide()) {
            ItemStack heldItem = player.getItemInHand(hand);
            boolean itemUsed = heldItem.getItem().equals(Items.BUCKET) && FluidUtil.interactWithFluidHandler(player, hand, world, pos, null);

            if (!itemUsed) {
                BlockEntity tileEntity = world.getBlockEntity(pos);
                if (tileEntity instanceof TieredCentrifugeBlockEntity blockEntity) {
                    this.openGui((ServerPlayer)player, blockEntity);
                }
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void openGui(ServerPlayer player, TieredCentrifugeBlockEntity tileEntity) {
        NetworkHooks.openScreen(player, tileEntity, (packetBuffer) -> {
            packetBuffer.writeBlockPos(tileEntity.getBlockPos());
        });
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, getBlockEntity(), (level1, pos1, state1, blockEntity1) -> TieredCentrifugeBlockEntity.tick(level1, pos1, state1, (TieredCentrifugeBlockEntity) blockEntity1));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @javax.annotation.Nullable BlockGetter getter, @NotNull List<Component> components, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, getter, components, flag);
        if(Screen.hasShiftDown()) {
            if (tier == CentrifugeTiers.CREATIVE) {
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.creative_centrifuge.needsNoEnergy")
                        .withStyle(ChatFormatting.YELLOW));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.creative_centrifuge.oneTickPerComb")
                        .withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.outputMultiplier", tier.getOutputMultiplier())
                        .withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.inputSlots", tier.getInputSlotAmount())
                        .withStyle(ChatFormatting.AQUA));
            } else {
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.fasterThanHeated", tier.getSpeed())
                        .withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.outputMultiplier", tier.getOutputMultiplier())
                        .withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.inputSlots", tier.getInputSlotAmount())
                        .withStyle(ChatFormatting.AQUA));
                components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.energyCapacity", String.format("%,d", tier.getEnergyCapacity()))
                        .withStyle(ChatFormatting.AQUA));
            }

            components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.fluidCapacity", String.format("%,d", tier.getFluidCapacity()))
                    .withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.maxStackSize", String.format("%,d", tier.getItemMaxStackSize()))
                    .withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.centrifuge.pressShiftForMore")
                    .withStyle(ChatFormatting.YELLOW));
        }
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
}
