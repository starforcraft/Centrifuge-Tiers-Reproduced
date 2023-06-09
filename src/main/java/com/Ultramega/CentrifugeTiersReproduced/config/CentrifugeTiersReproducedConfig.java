package com.Ultramega.CentrifugeTiersReproduced.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CentrifugeTiersReproducedConfig {
    public static ForgeConfigSpec.IntValue HIGH_END_CENTRIFUGE_SPEED;
    public static ForgeConfigSpec.IntValue HIGH_END_CENTRIFUGE_ITEM_MAX_STACK_SIZE;
    public static ForgeConfigSpec.IntValue HIGH_END_CENTRIFUGE_OUTPUT_MULTIPLIER;
    public static ForgeConfigSpec.IntValue HIGH_END_CENTRIFUGE_ENERGY_CAPACITY;
    public static ForgeConfigSpec.IntValue HIGH_END_CENTRIFUGE_FLUID_CAPACITY;

    public static ForgeConfigSpec.IntValue NUCLEAR_CENTRIFUGE_SPEED;
    public static ForgeConfigSpec.IntValue NUCLEAR_CENTRIFUGE_ITEM_MAX_STACK_SIZE;
    public static ForgeConfigSpec.IntValue NUCLEAR_CENTRIFUGE_OUTPUT_MULTIPLIER;
    public static ForgeConfigSpec.IntValue NUCLEAR_CENTRIFUGE_ENERGY_CAPACITY;
    public static ForgeConfigSpec.IntValue NUCLEAR_CENTRIFUGE_FLUID_CAPACITY;

    public static ForgeConfigSpec.IntValue COSMIC_CENTRIFUGE_SPEED;
    public static ForgeConfigSpec.IntValue COSMIC_CENTRIFUGE_ITEM_MAX_STACK_SIZE;
    public static ForgeConfigSpec.IntValue COSMIC_CENTRIFUGE_OUTPUT_MULTIPLIER;
    public static ForgeConfigSpec.IntValue COSMIC_CENTRIFUGE_ENERGY_CAPACITY;
    public static ForgeConfigSpec.IntValue COSMIC_CENTRIFUGE_FLUID_CAPACITY;

    public static ForgeConfigSpec.IntValue CREATIVE_CENTRIFUGE_ITEM_MAX_STACK_SIZE;
    public static ForgeConfigSpec.IntValue CREATIVE_CENTRIFUGE_OUTPUT_MULTIPLIER;
    public static ForgeConfigSpec.IntValue CREATIVE_CENTRIFUGE_FLUID_CAPACITY;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.comment("Centrifuge Tiers Reproduced Options");

        HIGH_END_CENTRIFUGE_SPEED = builder
            .comment("\nHigh End Centrifuge Speed")
            .defineInRange("highEndCentrifugeSpeed", 5, 1, Integer.MAX_VALUE);
        HIGH_END_CENTRIFUGE_ITEM_MAX_STACK_SIZE = builder
                .comment("\nHigh End Centrifuge Item Max Stack Size")
                .defineInRange("highEndCentrifugeItemMaxStackSize", 256, 1, Integer.MAX_VALUE);
        HIGH_END_CENTRIFUGE_OUTPUT_MULTIPLIER = builder
                .comment("\nHigh End Centrifuge Output Multiplier")
                .defineInRange("highEndCentrifugeOutputMultiplier", 2, 1, Integer.MAX_VALUE);
        HIGH_END_CENTRIFUGE_ENERGY_CAPACITY = builder
            .comment("\nHigh End Centrifuge Energy Capacity")
            .defineInRange("highEndCentrifugeEnergyCapacity", 100000, 1, Integer.MAX_VALUE);
        HIGH_END_CENTRIFUGE_FLUID_CAPACITY = builder
            .comment("\nHigh End Centrifuge Fluid Capacity")
            .defineInRange("highEndCentrifugeFluidCapacity", 100000, 1, Integer.MAX_VALUE);

        NUCLEAR_CENTRIFUGE_SPEED = builder
                .comment("\nNuclear Centrifuge Speed")
                .defineInRange("nuclearCentrifugeSpeed", 8, 1, Integer.MAX_VALUE);
        NUCLEAR_CENTRIFUGE_ITEM_MAX_STACK_SIZE = builder
                .comment("\nNuclear Centrifuge Item Max Stack Size")
                .defineInRange("nuclearCentrifugeItemMaxStackSize", 512, 1, Integer.MAX_VALUE);
        NUCLEAR_CENTRIFUGE_OUTPUT_MULTIPLIER = builder
                .comment("\nNuclear Centrifuge Output Multiplier")
                .defineInRange("nuclearCentrifugeOutputMultiplier", 4, 1, Integer.MAX_VALUE);
        NUCLEAR_CENTRIFUGE_ENERGY_CAPACITY = builder
                .comment("\nNuclear Centrifuge Energy Capacity")
                .defineInRange("nuclearCentrifugeEnergyCapacity", 200000, 1, Integer.MAX_VALUE);
        NUCLEAR_CENTRIFUGE_FLUID_CAPACITY = builder
                .comment("\nNuclear Centrifuge Fluid Capacity")
                .defineInRange("nuclearCentrifugeFluidCapacity", 200000, 1, Integer.MAX_VALUE);

        COSMIC_CENTRIFUGE_SPEED = builder
                .comment("\nCosmic Centrifuge Speed")
                .defineInRange("cosmicCentrifugeSpeed", 12, 1, Integer.MAX_VALUE);
        COSMIC_CENTRIFUGE_ITEM_MAX_STACK_SIZE = builder
                .comment("\nCosmic Centrifuge Item Max Stack Size")
                .defineInRange("cosmicCentrifugeItemMaxStackSize", 1024, 1, Integer.MAX_VALUE);
        COSMIC_CENTRIFUGE_OUTPUT_MULTIPLIER = builder
                .comment("\nCosmic Centrifuge Output Multiplier")
                .defineInRange("cosmicCentrifugeOutputMultiplier", 8, 1, Integer.MAX_VALUE);
        COSMIC_CENTRIFUGE_ENERGY_CAPACITY = builder
                .comment("\nCosmic Centrifuge Energy Capacity")
                .defineInRange("cosmicCentrifugeEnergyCapacity", 600000, 1, Integer.MAX_VALUE);
        COSMIC_CENTRIFUGE_FLUID_CAPACITY = builder
                .comment("\nCosmic Centrifuge Fluid Capacity")
                .defineInRange("cosmicCentrifugeFluidCapacity", 600000, 1, Integer.MAX_VALUE);

        CREATIVE_CENTRIFUGE_ITEM_MAX_STACK_SIZE = builder
                .comment("\nCreative Centrifuge Item Max Stack Size")
                .defineInRange("creativeCentrifugeItemMaxStackSize", 16384, 1, Integer.MAX_VALUE);
        CREATIVE_CENTRIFUGE_OUTPUT_MULTIPLIER = builder
                .comment("\nCreative Centrifuge Output Multiplier")
                .defineInRange("creativeCentrifugeOutputMultiplier", 100, 1, Integer.MAX_VALUE);
        CREATIVE_CENTRIFUGE_FLUID_CAPACITY = builder
                .comment("\nCreative Centrifuge Fluid Capacity")
                .defineInRange("creativeCentrifugeFluidCapacity", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);
    }
}
