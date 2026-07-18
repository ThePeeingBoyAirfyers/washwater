package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.basic.BasicFluidRendererFactory;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WWFluidRendererFactory extends LevelConfigurationInterface<WWFluidRendererFactory> {
    LevelConfigurationRegistry<WWFluidRendererFactory> REGISTRY =
            LevelConfigurationRegistry.create(WashWater.resource("fluid_renderer"), LevelConfigurationSide.CLIENT, BasicFluidRendererFactory::new);

    @NotNull WWFluidRenderer create(
            @Nullable WWFluidRenderer currentRenderer,
            @NotNull FluidRenderingState state
    );
}
