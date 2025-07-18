package com.thepeeingboyairfryers.washwater.client;

import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.model.quad.blender.BlendedColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.services.FluidRendererFactory;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class WashFluidRendererFactory implements FluidRendererFactory {
    @Override
    public FluidRenderer createPlatformFluidRenderer(ColorProviderRegistry colorProviderRegistry, LightPipelineProvider lightPipelineProvider) {
        return null;
    }

    @Override
    public BlendedColorProvider<FluidState> getWaterColorProvider() {
        return null;
    }

    @Override
    public BlendedColorProvider<BlockState> getWaterBlockColorProvider() {
        return null;
    }
}
