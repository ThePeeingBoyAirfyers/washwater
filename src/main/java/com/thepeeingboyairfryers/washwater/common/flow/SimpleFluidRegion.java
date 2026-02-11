package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidRegion implements FluidRegion {
    private final Long2ObjectMap<FluidSection> fluidSections = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<LevelChunkSection> sections = new Long2ObjectOpenHashMap<>();
    private final ServerLevel level;
    private final LongConsumer onUpdate;

    public SimpleFluidRegion(ServerLevel iLevel, LongConsumer iOnUpdate) {
        this.level = iLevel;
        this.onUpdate =  iOnUpdate;
    }

    @Override
    public int getFluidVolume(int x, int y, int z, FluidType type) {
        return getFluidSection(x, y, z).getVolumeOf(x & 15, y & 15, z & 15, type);
    }

    @Override
    public @NotNull MultiFluidValue getFluids(int x, int y, int z) {
        return getFluidSection(x, y, z).getVolume(x & 15, y & 15, z & 15);
    }

    @Override
    public int getAllVolume(int x, int y, int z) {
        return getFluidSection(x, y, z).getAllVolume(x & 15, y & 15, z & 15);
    }


    @Override
    public boolean isAir(int x, int y, int z) {
        return getFluidSection(x, y, z).getAllVolume(x & 15, y & 15, z & 15) == 0;
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        return !getSection(x, y, z).getBlockState(x & 15, y & 15, z & 15).isAir();
    }

    @Override
    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        getFluidSection(x, y, z).setVolume(x & 15, y & 15, z & 15, value);

        onUpdate.accept(BlockPos.asLong(x, y, z));
        for (Direction direction : Direction.values()) {
            onUpdate.accept(
                    BlockPos.asLong(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ())
            );
        }
    }

    private FluidSection getFluidSection(int x, int y, int z) {
        return FluidSectionManager.getIfAbsent(level, fluidSections, x >> 4, y >> 4, z >> 4);
    }

    private LevelChunkSection getSection(int x, int y, int z) {
        return sections.computeIfAbsent(SectionPos.asLong(x, y, z), l -> level.getChunk(x, z).getSection(level.getSectionIndex(y)));
    }
}
