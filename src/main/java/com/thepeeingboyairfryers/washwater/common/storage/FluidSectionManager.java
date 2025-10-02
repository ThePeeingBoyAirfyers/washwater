package com.thepeeingboyairfryers.washwater.common.storage;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.FluidChunkAttachment;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
        }));
    }

    public static FluidChunkAttachment getAttachmentFor(LevelChunk chunk) {
        var result = chunk.getData(WWAttachments.FLUID_CHUNK);
        result.configure(chunk);
        return result;
    }

    public static void writeStateToFluidSection(FluidSection fluidSection, int x, int y, int z, BlockState state) {
        if (!state.getFluidState().isEmpty()) {
            fluidSection.setVolume(x, y, z, MultiFluidValue.single(Fluids.WATER.getFluidType(), (short) 1000));
        }

    }

    public static BlockState getBlockStateFromFluidSection(FluidSection fluidSection, int x, int y, int z) {
        if (fluidSection.getAllVolume(x, y, z) > 0) {
            return Fluids.WATER.defaultFluidState().createLegacyBlock();
        } else return Blocks.AIR.defaultBlockState();
    }

    public static FluidState getFluidStateFromFluidSection(FluidSection fluidSection, int x, int y, int z) {
        if (fluidSection.getAllVolume(x, y, z) > 0) {
            return Fluids.WATER.defaultFluidState();
        } else return  Fluids.EMPTY.defaultFluidState();
    }

    public static FluidSection getIfAbsent(Level level, Long2ObjectMap<FluidSection> sections, int x, int y, int z) {
        return sections.computeIfAbsent(
                SectionPos.asLong(x, y, z),
                k -> getAttachmentFor(level.getChunk(x, z)).getSectionWithY(y)
        );
    }
}
