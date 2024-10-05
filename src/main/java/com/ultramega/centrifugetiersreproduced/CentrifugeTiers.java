package com.ultramega.centrifugetiersreproduced;

import com.ultramega.centrifugetiersreproduced.utils.MultiBlockInfo;
import net.minecraft.util.StringRepresentable;

public enum CentrifugeTiers implements StringRepresentable {
    TIER_1("tier_1", "3x3x3", -1, -1, -1, 20000, 20000, 1, 0, new MultiBlockInfo(26, 3, 3, 3, -1, -1, -2)),
    TIER_2("tier_2", "3x3x4", -1, -1, -1, 40000, 40000, 3, 3, new MultiBlockInfo(35, 3, 4, 3, -1, -1, -2)),
    TIER_3("tier_3", "5x5x5", -1, -1, -1, 40000, 40000, 3, 3, new MultiBlockInfo(124, 5, 5, 5, -2, -1, -4)),
    TIER_4("tier_4", "5x5x6", -1, -1, -1, 60000, 60000, 5, 9, new MultiBlockInfo(149, 5, 6, 5, -2, -1, -4));

    public static final StringRepresentable.EnumCodec<CentrifugeTiers> CODEC = StringRepresentable.fromEnum(CentrifugeTiers::values);

    private final String name;
    private final String multiblockSize;
    private int speed;
    private int outputMultiplier;
    private int itemMaxStackSize;
    private final int fluidCapacity;
    private final int energyCapacity;
    private final int inputSlotAmountIncrease;
    private final int outputSlotAmountIncrease;
    private final MultiBlockInfo multiblockInfo;

    CentrifugeTiers(String name, String multiblockSize, int speed, int outputMultiplier, int itemMaxStackSize, int fluidCapacity, int energyCapacity, int inputSlotAmountIncrease, int outputSlotAmountIncrease, MultiBlockInfo multiblockInfo) {
        this.name = name;
        this.multiblockSize = multiblockSize;
        this.speed = speed;
        this.outputMultiplier = outputMultiplier;
        this.itemMaxStackSize = itemMaxStackSize;
        this.fluidCapacity = fluidCapacity;
        this.energyCapacity = energyCapacity;
        this.inputSlotAmountIncrease = inputSlotAmountIncrease;
        this.outputSlotAmountIncrease = outputSlotAmountIncrease;
        this.multiblockInfo = multiblockInfo;
    }

    public String getMultiblockSize() {
        return multiblockSize;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setOutputMultiplier(int outputMultiplier) {
        this.outputMultiplier = outputMultiplier;
    }

    public int getOutputMultiplier() {
        return outputMultiplier;
    }

    public void setItemMaxStackSize(int itemMaxStackSize) {
        this.itemMaxStackSize = itemMaxStackSize;
    }

    public int getItemMaxStackSize() {
        return itemMaxStackSize;
    }

    public int getFluidCapacity() {
        return fluidCapacity;
    }

    public int getEnergyCapacity() {
        return energyCapacity;
    }

    public int getInputSlotAmountIncrease() {
        return inputSlotAmountIncrease;
    }

    public int getOutputSlotAmountIncrease() {
        return outputSlotAmountIncrease;
    }

    public MultiBlockInfo getMultiblockInfo() {
        return multiblockInfo;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
