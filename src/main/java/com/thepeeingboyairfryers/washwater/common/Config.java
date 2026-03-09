package com.thepeeingboyairfryers.washwater.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue PARALLEL = BUILDER
            .comment("Should fluid be simulated on multiple threads")
            .define("multithread", true);
    public static final ModConfigSpec.IntValue THREAD_COUNT = BUILDER
            .comment("How many threads we should use to simulate fluids")
            .defineInRange("threadCount", 8, 1, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue WATER_SOURCE_GAIN = BUILDER
            .comment("Water source gain per randomtick")
            .defineInRange("waterSourceGain", 1, 0, Integer.MAX_VALUE);
    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
        throw new IllegalStateException();
    }
}
