package com.thepeeingboyairfryers.washwater;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue PARALLEL = BUILDER
            .comment("Should fluid be simulated on multiple threads")
            .define("multithread", true);
    public static final ModConfigSpec.IntValue THREAD_COUNT = BUILDER
            .comment("How many threads we should use to simulate fluids, 0 means let WW decide")
            .defineInRange("threadCount", 0, 0, Integer.MAX_VALUE);
    public static final ModConfigSpec.IntValue WATER_SOURCE_GAIN = BUILDER
            .comment("Water source gain per randomtick")
            .defineInRange("waterSourceGain", 1, 0, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue SEND_UPDATE_PACKETS_EVERY = BUILDER
            .comment("How often should we send update packets to all clients?, in ticks")
            .defineInRange("sendUpdatePacketEvery", 2, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue PRECISION_BUCKET_CAPACITY = BUILDER
            .comment("How many units of volume the precision bucket should hold")
            .defineInRange("precisionBucketCapacity", 1000, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue VOLUME_PER_BLOCK = BUILDER
            .comment("How many units of volume should be in a block")
            .defineInRange("volumePerBlock", 1000, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec.IntValue MAX_PUSHING_DISTANCE = BUILDER
            .comment("How far should water be able to be pushed (by pistons etc.)")
            .defineInRange("maxPushingDistance", 8, 0, Integer.MAX_VALUE);


    static final ModConfigSpec SPEC = BUILDER.build();

    private Config() {
        throw new IllegalStateException();
    }
}
