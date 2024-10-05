package com.ultramega.centrifugetiersreproduced.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.blockentity.TieredCentrifugeCasingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TieredCentrifugeCasingBlock extends BaseEntityBlock {
    public static final MapCodec<TieredCentrifugeCasingBlock> CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(CentrifugeTiers.CODEC.fieldOf("tier").forGetter(TieredCentrifugeCasingBlock::getTier), propertiesCodec())
                    .apply(instance, TieredCentrifugeCasingBlock::new));

    private final CentrifugeTiers tier;

    public TieredCentrifugeCasingBlock(CentrifugeTiers tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof TieredCentrifugeCasingBlockEntity casingBlockEntity &&
                casingBlockEntity.isLinked() &&
                level.getBlockState(casingBlockEntity.getControllerPos()).getBlock() instanceof TieredCentrifugeControllerBlock controllerBlock) {
            return controllerBlock.useWithoutItem(state, level, casingBlockEntity.getControllerPos(), player, hitResult);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TieredCentrifugeCasingBlockEntity(tier, blockPos, blockState);
    }

    public CentrifugeTiers getTier() {
        return tier;
    }
}
