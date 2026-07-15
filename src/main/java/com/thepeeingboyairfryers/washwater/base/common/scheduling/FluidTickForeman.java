package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.FixedWorkForeman;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationSide;
import net.minecraft.server.level.ServerLevel;

public interface FluidTickForeman extends LevelConfigurationInterface<FluidTickForeman> {
    LevelConfigurationRegistry<FluidTickForeman> REGISTRY = LevelConfigurationRegistry.create(
            WashWater.resource("foreman"),
            LevelConfigurationSide.SERVER,
            () -> new FixedWorkForeman(4)
    );

    int calculateWork(ServerLevel level, int currentProgress, FluidTickStrategy strategy);
}
