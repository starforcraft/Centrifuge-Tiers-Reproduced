package com.ultramega.centrifugetiersreproduced.config;

import com.ultramega.centrifugetiersreproduced.CentrifugeTiers;
import com.ultramega.centrifugetiersreproduced.CentrifugeTiersReproduced;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = CentrifugeTiersReproduced.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue TIER_1_CENTRIFUGE_SPEED = BUILDER
            .comment("Tier 1 Centrifuge Speed")
            .defineInRange("tier1CentrifugeSpeed", 1, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_2_CENTRIFUGE_SPEED = BUILDER
            .comment("Tier 2 Centrifuge Speed")
            .defineInRange("tier2CentrifugeSpeed", 2, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_3_CENTRIFUGE_SPEED = BUILDER
            .comment("Tier 3 Centrifuge Speed")
            .defineInRange("tier3CentrifugeSpeed", 3, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_4_CENTRIFUGE_SPEED = BUILDER
            .comment("Tier 4 Centrifuge Speed")
            .defineInRange("tier4CentrifugeSpeed", 4, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue TIER_1_CENTRIFUGE_OUTPUT_MULTIPLIER = BUILDER
            .comment("Tier 1 Centrifuge Output Multiplier")
            .defineInRange("tier1CentrifugeOutputMultiplier", 1, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_2_CENTRIFUGE_OUTPUT_MULTIPLIER = BUILDER
            .comment("Tier 2 Centrifuge Output Multiplier")
            .defineInRange("tier2CentrifugeOutputMultiplier", 2, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_3_CENTRIFUGE_OUTPUT_MULTIPLIER = BUILDER
            .comment("Tier 3 Centrifuge Output Multiplier")
            .defineInRange("tier3CentrifugeOutputMultiplier", 2, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_4_CENTRIFUGE_OUTPUT_MULTIPLIER = BUILDER
            .comment("Tier 4 Centrifuge Output Multiplier")
            .defineInRange("tier4CentrifugeOutputMultiplier", 3, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.IntValue TIER_1_CENTRIFUGE_MAX_STACK_SIZE = BUILDER
            .comment("Tier 1 Centrifuge Max Stack Size")
            .defineInRange("tier1CentrifugeMaxStackSize", 256, 64, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_2_CENTRIFUGE_MAX_STACK_SIZE = BUILDER
            .comment("Tier 2 Centrifuge Max Stack Size")
            .defineInRange("tier2CentrifugeMaxStackSize", 256, 64, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_3_CENTRIFUGE_MAX_STACK_SIZE = BUILDER
            .comment("Tier 3 Centrifuge Max Stack Size")
            .defineInRange("tier3CentrifugeMaxStackSize", 512, 64, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue TIER_4_CENTRIFUGE_MAX_STACK_SIZE = BUILDER
            .comment("Tier 4 Centrifuge Max Stack Size")
            .defineInRange("tier4CentrifugeMaxStackSize", 512, 64, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        CentrifugeTiers.TIER_1.setSpeed(TIER_1_CENTRIFUGE_SPEED.get());
        CentrifugeTiers.TIER_2.setSpeed(TIER_2_CENTRIFUGE_SPEED.get());
        CentrifugeTiers.TIER_3.setSpeed(TIER_3_CENTRIFUGE_SPEED.get());
        CentrifugeTiers.TIER_4.setSpeed(TIER_4_CENTRIFUGE_SPEED.get());

        CentrifugeTiers.TIER_1.setOutputMultiplier(TIER_1_CENTRIFUGE_OUTPUT_MULTIPLIER.get());
        CentrifugeTiers.TIER_2.setOutputMultiplier(TIER_2_CENTRIFUGE_OUTPUT_MULTIPLIER.get());
        CentrifugeTiers.TIER_3.setOutputMultiplier(TIER_3_CENTRIFUGE_OUTPUT_MULTIPLIER.get());
        CentrifugeTiers.TIER_4.setOutputMultiplier(TIER_4_CENTRIFUGE_OUTPUT_MULTIPLIER.get());

        CentrifugeTiers.TIER_1.setItemMaxStackSize(TIER_1_CENTRIFUGE_MAX_STACK_SIZE.get());
        CentrifugeTiers.TIER_2.setItemMaxStackSize(TIER_2_CENTRIFUGE_MAX_STACK_SIZE.get());
        CentrifugeTiers.TIER_3.setItemMaxStackSize(TIER_3_CENTRIFUGE_MAX_STACK_SIZE.get());
        CentrifugeTiers.TIER_4.setItemMaxStackSize(TIER_4_CENTRIFUGE_MAX_STACK_SIZE.get());
    }
}
