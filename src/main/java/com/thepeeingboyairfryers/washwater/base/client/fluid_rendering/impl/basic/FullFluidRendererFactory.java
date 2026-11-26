package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.basic;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRenderer;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRendererFactory;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FullFluidRendererFactory implements WWFluidRendererFactory {
    public static final FullFluidRendererFactory INSTANCE = new FullFluidRendererFactory();
    public static final MapCodec<FullFluidRendererFactory> CODEC =  MapCodec.unit(INSTANCE);
    public static final ConfigurationCommand<FullFluidRendererFactory> COMMAND = (ctx, data) -> INSTANCE;

    @Override
    public @NotNull WWFluidRenderer create(@Nullable WWFluidRenderer currentRenderer, @NotNull FluidRenderingState state) {
        if (currentRenderer != null && currentRenderer.getClass() == FullFluidRenderer.class)
            return currentRenderer;

        return new FullFluidRenderer(state);
    }

    @Override
    public MapCodec<? extends WWFluidRendererFactory> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends WWFluidRendererFactory> command() {
        return COMMAND;
    }
}
