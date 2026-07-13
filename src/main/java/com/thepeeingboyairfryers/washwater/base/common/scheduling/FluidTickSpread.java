package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.FixedWorkTickSpread;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import net.minecraft.server.level.ServerLevel;

public interface FluidTickSpread extends LevelConfigurationInterface<FluidTickSpread> {
    LevelConfigurationRegistry<FluidTickSpread> REGISTRY = LevelConfigurationRegistry.create(
            WashWater.resource("spread"),
            () -> new FixedWorkTickSpread(4)
    );

    int calculateWork(ServerLevel level, int currentProgress, FluidTickStrategy strategy);
}
