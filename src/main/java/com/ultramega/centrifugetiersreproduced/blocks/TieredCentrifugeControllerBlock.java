package com.ultramega.centrifugetiersreproduced.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeControllerBlockEntity;
import cy.jdkdigital.productivelib.common.block.CapabilityContainerBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;

import java.util.List;

public class TieredCentrifugeControllerBlock extends CapabilityContainerBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty PROPERTY_VALID = BooleanProperty.create("valid");
    public static final MapCodec<TieredCentrifugeControllerBlock> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(CentrifugeTiers.CODEC.fieldOf("tier").forGetter(TieredCentrifugeControllerBlock::getTier), propertiesCodec())
                    .apply(instance, TieredCentrifugeControllerBlock::new));

    private final CentrifugeTiers tier;

    public TieredCentrifugeControllerBlock(CentrifugeTiers tier, BlockBehaviour.Properties properties) {
        super(properties);
        this.tier = tier;
        this.registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH).setValue(PROPERTY_VALID, false));
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return level.isClientSide ? null : createTickerHelper(blockEntityType, CentrifugeTiersReproduced.getControllerBlockEntityType(tier), TieredCentrifugeControllerBlockEntity::tick);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.getBlockState(pos).getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID) && !level.isClientSide() && FluidUtil.interactWithFluidHandler(player, hand, level, pos, null)) {
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TieredCentrifugeControllerBlockEntity centrifugeBlockEntity && level.getBlockState(pos).getValue(TieredCentrifugeControllerBlock.PROPERTY_VALID)) {
            if (!level.isClientSide()) {
                player.openMenu(centrifugeBlockEntity, pos);
            }
            return InteractionResult.SUCCESS_NO_ITEM_USED;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> components, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.input_slots", 1 + tier.getInputSlotAmountIncrease())
                    .withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.processing_speed", tier.getSpeed())
                    .withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.output_multiplier", tier.getOutputMultiplier())
                    .withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.item_max_stack_size", tier.getItemMaxStackSize())
                    .withStyle(ChatFormatting.AQUA));
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.multiblock_size", tier.getMultiblockSize())
                    .withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.translatable("tooltip.centrifugetiersreproduced.hold_shift")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TieredCentrifugeControllerBlockEntity(tier, blockPos, blockState);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PROPERTY_VALID);
    }

    public CentrifugeTiers getTier() {
        return tier;
    }
}
