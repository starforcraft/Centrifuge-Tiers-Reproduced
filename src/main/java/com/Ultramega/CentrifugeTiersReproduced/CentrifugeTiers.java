package com.Ultramega.CentrifugeTiersReproduced;

import com.Ultramega.CentrifugeTiersReproduced.config.CentrifugeTiersReproducedConfig;

public enum CentrifugeTiers {
    HIGH_END("high_end", CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_SPEED.get(), CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get(), CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_OUTPUT_MULTIPLIER.get(), CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_ENERGY_CAPACITY.get(), CentrifugeTiersReproducedConfig.HIGH_END_CENTRIFUGE_FLUID_CAPACITY.get(), 2),
    NUCLEAR("nuclear", CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_SPEED.get(), CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get(), CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_OUTPUT_MULTIPLIER.get(), CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_ENERGY_CAPACITY.get(), CentrifugeTiersReproducedConfig.NUCLEAR_CENTRIFUGE_FLUID_CAPACITY.get(), 3),
    COSMIC("cosmic", CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_SPEED.get(), CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get(), CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_OUTPUT_MULTIPLIER.get(), CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_ENERGY_CAPACITY.get(), CentrifugeTiersReproducedConfig.COSMIC_CENTRIFUGE_FLUID_CAPACITY.get(), 4),
    CREATIVE("creative", -1, CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_ITEM_MAX_STACK_SIZE.get(), CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_OUTPUT_MULTIPLIER.get(), -1, CentrifugeTiersReproducedConfig.CREATIVE_CENTRIFUGE_FLUID_CAPACITY.get(), 4);

    private final String name;
    private final int speed;
    private final int itemMaxStackSize;
    private final int outputMultiplier;
    private final int energyCapacity;
    private final int fluidCapacity;
    private final int inputSlotAmount;

    CentrifugeTiers(String name, int speed, int itemMaxStackSize, int outputMultiplier, int energyCapacity, int fluidCapacity, int inputSlotAmount) {
        this.name = name;
        this.speed = speed;
        this.itemMaxStackSize = itemMaxStackSize;
        this.outputMultiplier = outputMultiplier;
        this.energyCapacity = energyCapacity;
        this.fluidCapacity = fluidCapacity;
        this.inputSlotAmount = inputSlotAmount;
    }

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public int getItemMaxStackSize() {
        return itemMaxStackSize;
    }

    public int getOutputMultiplier() {
        return outputMultiplier;
    }

    public int getEnergyCapacity() {
        return energyCapacity;
    }

    public int getFluidCapacity() {
        return fluidCapacity;
    }

    public int getInputSlotAmount() {
        return inputSlotAmount;
    }
}
