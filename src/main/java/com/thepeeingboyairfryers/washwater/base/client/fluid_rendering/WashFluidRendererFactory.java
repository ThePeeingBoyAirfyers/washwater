package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering;

import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.model.quad.blender.BlendedColorProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.services.FluidRendererFactory;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class WashFluidRendererFactory implements FluidRendererFactory {
    @Override
    public FluidRenderer createPlatformFluidRenderer(
            ColorProviderRegistry colorProviderRegistry,
            LightPipelineProvider lightPipelineProvider
    ) {
        return new WashFluidRenderer(colorProviderRegistry, lightPipelineProvider);
    }

    @Override
    public BlendedColorProvider<FluidState> getWaterColorProvider() {
        final IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(Fluids.WATER);
        return new BlendedColorProvider<>() {
            protected int getColor(LevelSlice slice, FluidState state, BlockPos pos) {
                return ext.getTintColor(state, slice, pos);
            }
        };
    }

    @Override
    public BlendedColorProvider<BlockState> getWaterBlockColorProvider() {
        final IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(Fluids.WATER);
        return new BlendedColorProvider<>() {
            protected int getColor(LevelSlice slice, BlockState state, BlockPos pos) {
                return ext.getTintColor(state.getFluidState().isEmpty() ? Fluids.WATER.defaultFluidState() : state.getFluidState(), slice, pos);
            }
        };
    }
}
