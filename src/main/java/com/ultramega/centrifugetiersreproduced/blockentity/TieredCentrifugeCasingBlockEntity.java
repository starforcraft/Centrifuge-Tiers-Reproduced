package com.ultramega.centrifugetiersreproduced.blockentity;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import cy.jdkdigital.productivelib.common.block.entity.AbstractBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TieredCentrifugeCasingBlockEntity extends AbstractBlockEntity {
    private final CentrifugeTiers tier;

    private BlockPos controllerPos;

    public TieredCentrifugeCasingBlockEntity(CentrifugeTiers tier, BlockPos pos, BlockState blockState) {
        super(CentrifugeTiersReproduced.getCasingBlockEntityType(tier), pos, blockState);
        this.tier = tier;
    }

    @Override
    public void setRemoved() {
        TieredCentrifugeControllerBlockEntity controller = getController();
        if (controller != null) {
            controller.invalidateStructure();
        }
        super.setRemoved();
    }

    public TieredCentrifugeControllerBlockEntity getController() {
        if (isLinked() && this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(controllerPos);
            if (blockEntity instanceof TieredCentrifugeControllerBlockEntity controllerBlockEntity) {
                return controllerBlockEntity;
            } else {
                setControllerPos(null);
            }
        }
        return null;
    }

    @Override
    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.savePacketNBT(tag, provider);

        if (isLinked()) tag.put("controllerPos", NbtUtils.writeBlockPos(controllerPos));
    }

    @Override
    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadPacketNBT(tag, provider);

        NbtUtils.readBlockPos(tag, "controllerPos").ifPresent(pos -> this.controllerPos = pos);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadPacketNBT(tag, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = super.getUpdateTag(provider);
        savePacketNBT(tag, provider);

        return tag;
    }

    public void setControllerPos(BlockPos controllerPos) {
        this.controllerPos = controllerPos;
        this.level.invalidateCapabilities(getBlockPos());
    }

    public BlockPos getControllerPos() {
        return controllerPos;
    }

    public boolean isLinked() {
        return controllerPos != null;
    }

    public CentrifugeTiers getTier() {
        return tier;
    }
}
