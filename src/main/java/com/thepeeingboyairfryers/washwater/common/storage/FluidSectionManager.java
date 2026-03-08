package com.thepeeingboyairfryers.washwater.common.storage;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.FluidChunkAttachment;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.common.util.WWBlockState;
import com.thepeeingboyairfryers.washwater.common.util.WWFluidState;
import com.thepeeingboyairfryers.washwater.duck.IFluidState;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

public class FluidSectionManager {
    private FluidSectionManager() {
        throw new IllegalStateException();
    }

    public static void register(IEventBus bus) {
        bus.addListener((NewRegistryEvent e) -> e.register(FluidSection.DISPATCH_REGISTRY));
        bus.addListener((RegisterEvent e) -> e.register(FluidSection.DISPATCH_KEY, r -> {
            r.register(WashWater.resource("empty"), FluidSection.EMPTY.codec());
            r.register(WashWater.resource("dumb"), DumbFluidSection.CODEC);
            r.register(WashWater.resource("single_fluid"), SingleFluidSection.CODEC);
        }));
    }

    public static FluidChunkAttachment getAttachmentFor(LevelChunk chunk) {
        synchronized (chunk) { // TODO this is needed for chunkbuilding threads
            var result = chunk.getData(WWAttachments.FLUID_CHUNK);
            result.configure(chunk);
            return result;
        }
    }

    public static BlockState writeStateToFluidSection(FluidSection fluidSection, int x, int y, int z, BlockState state) {
        var iState = (IFluidState) state.getFluidState();
        fluidSection.setVolume(x, y, z, iState.ww€getFluid());
        if (state.is(Blocks.WATER)) return Blocks.AIR.defaultBlockState();
        if (state instanceof WWBlockState ww) return ww.ww€getOG();
        return state;
    }

    public static BlockState getBlockStateFromFluidSection(FluidSection fluidSection, int x, int y, int z, BlockState og) {
        MultiFluidValue result = fluidSection.getVolume(x, y, z);

        if (result.isEmpty()) return og;
        if (og.isAir() && result.getTotalVolume() > 100)
            og = FluidManager.getFluidBlockState(result);
        return new WWBlockState(result, og);
    }

    public static FluidState getFluidStateFromFluidSection(FluidSection fluidSection, int x, int y, int z, FluidState og) {
        MultiFluidValue result = fluidSection.getVolume(x, y, z);

        if (result.isEmpty()) return og;
        if (og.isEmpty() && result.getTotalVolume() > 100)
            og = FluidManager.getFluidState(result);
        return new WWFluidState(result, og);
    }

    public static FluidSection getIfAbsent(Level level, Long2ObjectMap<FluidSection> sections, int x, int y, int z) {
        return sections.computeIfAbsent(
                SectionPos.asLong(x, y, z),
                k -> getAttachmentFor(level.getChunk(x, z)).getSectionWithY(y)
        );
    }
}
