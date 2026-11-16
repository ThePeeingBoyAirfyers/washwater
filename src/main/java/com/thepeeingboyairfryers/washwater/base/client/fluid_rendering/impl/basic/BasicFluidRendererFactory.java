package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.basic;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRenderer;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRendererFactory;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicFluidRendererFactory implements WWFluidRendererFactory {
    public static final BasicFluidRendererFactory INSTANCE = new BasicFluidRendererFactory();
    public static final MapCodec<BasicFluidRendererFactory> CODEC =  MapCodec.unit(INSTANCE);
    public static final ConfigurationCommand<BasicFluidRendererFactory> COMMAND = (ctx, data) -> INSTANCE;

    @Override
    public @NotNull WWFluidRenderer create(@Nullable WWFluidRenderer currentRenderer, @NotNull FluidRenderingState state) {
        if (currentRenderer != null && currentRenderer.getClass() == BasicFluidRenderer.class)
            return currentRenderer;

        return new BasicFluidRenderer(state);
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
