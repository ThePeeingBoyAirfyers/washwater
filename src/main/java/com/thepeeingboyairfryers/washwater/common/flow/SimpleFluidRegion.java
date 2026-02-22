package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidRegion implements FluidRegion {
    private final Long2ObjectMap<FluidSection> fluidSections = new Long2ObjectOpenHashMap<>();
    private final Long2ObjectMap<LevelChunkSection> sections = new Long2ObjectOpenHashMap<>();
    private final ServerLevel level;
    private LongSet onUpdate;

    public SimpleFluidRegion(ServerLevel iLevel) {
        this.level = iLevel;
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
        BlockState localBS = getSection(x, y, z).getBlockState(x & 15, y & 15, z & 15);
        return (!localBS.isAir() && localBS.getFluidState().isEmpty());
    }

    @Override
    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        getFluidSection(x, y, z).setVolume(x & 15, y & 15, z & 15, value);

        if (!value.isEmpty()) {
            onUpdate.add(BlockPos.asLong(x, y, z));
        } else {
            onUpdate.remove(BlockPos.asLong(x, y, z));
        }

        for (Direction direction : Direction.values()) {
            int xD = x + direction.getStepX();
            int yD = y + direction.getStepY();
            int zD = z + direction.getStepZ();
            if (isAir(xD, yD, zD)) continue;
            onUpdate.add(BlockPos.asLong(xD, yD, zD));
        }
    }

    private FluidSection getFluidSection(int x, int y, int z) {
        return FluidSectionManager.getIfAbsent(level, fluidSections, x >> 4, y >> 4, z >> 4);
    }

    private LevelChunkSection getSection(int x, int y, int z) {
        return sections.computeIfAbsent(SectionPos.asLong(x >> 4, y >> 4, z >> 4), l -> level.getChunk(x >> 4, z >> 4).getSection(level.getSectionIndex(y)));
    }

    public void setTickSet(LongSet current) {
        onUpdate = current;
    }
}
