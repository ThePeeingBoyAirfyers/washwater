package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickForeman;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.minecraft.server.level.ServerLevel;

public class ManualForeman implements FluidTickForeman {
    public static final ManualForeman INSTANCE = new ManualForeman();
    public static final MapCodec<ManualForeman> CODEC =  MapCodec.unit(INSTANCE);
    public static final ConfigurationCommand<ManualForeman> COMMAND = (ctx, data) -> INSTANCE;
    private boolean shouldStep = false;

    public void step() {
        shouldStep = true;
    }

    @Override
    public int calculateWork(ServerLevel level, int currentProgress, FluidTickStrategy strategy) {
        if (shouldStep) {
            shouldStep = false;
            return 8;
        }

        return 0;
    }

    @Override
    public MapCodec<? extends FluidTickForeman> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends FluidTickForeman> command() {
        return COMMAND;
    }
}
