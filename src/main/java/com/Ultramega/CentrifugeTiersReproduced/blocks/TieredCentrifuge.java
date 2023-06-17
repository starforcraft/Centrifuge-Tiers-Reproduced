package com.Ultramega.CentrifugeTiersReproduced.blocks;

import com.Ultramega.CentrifugeTiersReproduced.CentrifugeTiers;
import com.Ultramega.CentrifugeTiersReproduced.blockentity.TieredCentrifugeBlockEntity;
import com.Ultramega.CentrifugeTiersReproduced.registry.ModBlockEntityTypes;

import cy.jdkdigital.productivebees.common.block.PoweredCentrifuge;
import net.minecraft.core.BlockPos;
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
}
