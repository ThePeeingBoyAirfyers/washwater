package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.ducks.ILevelSliceConfiguration;
import com.thepeeingboyairfryers.washwater.ducks.ILevelSliceFluids;
import net.caffeinemc.mods.sodium.client.model.color.ColorProviderRegistry;
import net.caffeinemc.mods.sodium.client.model.light.LightPipelineProvider;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.FluidRenderer;
import net.caffeinemc.mods.sodium.client.render.chunk.translucent_sorting.TranslucentGeometryCollector;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;


public class SodiumWashFluidRenderer extends FluidRenderer {

    private final FluidRenderingState state;
    private WWFluidRenderer currentRenderer;


    public SodiumWashFluidRenderer(ColorProviderRegistry iColorRegistry, LightPipelineProvider iLightPipelineProvider) {
        this.state = new FluidRenderingState(iColorRegistry, iLightPipelineProvider);
    }

    @Override
    public void render(
            LevelSlice level,
            BlockState blockState,
            FluidState fluidState,
            BlockPos blockPos,
            BlockPos offset,
            TranslucentGeometryCollector iCollector,
            ChunkBuildBuffers buffers
    ) {
        checkRenderer(level);

        ILevelSliceFluids fluids = (ILevelSliceFluids) (Object) level;
        MultiFluidValue value = fluids.ww€getFluidFor(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (value.size() != 1) throw new IllegalArgumentException();
        MultiFluidValue.Entry entry = value.iterator().next();
        fluidState = FluidManager.dropinFluidState(entry.fluidType());

        state.configure(
                blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                offset,
                level,
                blockState,
                fluidState,
                iCollector,
                buffers
        );

        currentRenderer.render();
    }

    private void checkRenderer(LevelSlice level) {
        currentRenderer = ((ILevelSliceConfiguration) (Object) level).ww€getFactory().create(currentRenderer, state);
    }
}
