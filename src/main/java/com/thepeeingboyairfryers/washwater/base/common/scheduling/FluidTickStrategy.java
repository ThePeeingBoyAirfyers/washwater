package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.ParallelTickStrategy;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationSide;
import net.minecraft.server.level.ServerLevel;

public interface FluidTickStrategy extends LevelConfigurationInterface<FluidTickStrategy> {
    LevelConfigurationRegistry<FluidTickStrategy> REGISTRY = LevelConfigurationRegistry.create(
            WashWater.resource("tick_strategy"),
            LevelConfigurationSide.SERVER,
            () -> new ParallelTickStrategy(1200)
    );

    int tick(PerTickTimer.Context timerCtx, ServerLevel level, FluidTickingContext ctx, int prevProgress, int workAmount);

    void toBeTicked(ServerLevel level, int x, int y, int z);
}
