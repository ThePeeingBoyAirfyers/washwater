package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SimpleFluidRegion implements FluidRegion {
    private final Long2ObjectMap<FluidSection> sections = new Long2ObjectOpenHashMap<>();
    private final ServerLevel level;
    private final LongConsumer onUpdate;

    public SimpleFluidRegion(ServerLevel iLevel, LongConsumer iOnUpdate) {
        this.level = iLevel;
        this.onUpdate =  iOnUpdate;
    }

    @Override
    public int getFluidVolume(int x, int y, int z, FluidType type) {
        return getSection(x, y, z).getVolumeOf(x & 15, y & 15, z & 15, type);
    }

    @Override
    public @NotNull MultiFluidValue getFluids(int x, int y, int z) {
        return getSection(x, y, z).getVolume(x & 15, y & 15, z & 15);
    }

    @Override
    public boolean isAir(int x, int y, int z) {
        return getSection(x, y, z).getAllVolume(x & 15, y & 15, z & 15) == 0;
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        return false;
    }

    @Override
    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        getSection(x, y, z).setVolume(x & 15, y & 15, z & 15, value);

        onUpdate.accept(BlockPos.asLong(x, y, z));
        for (Direction direction : Direction.values()) {
            onUpdate.accept(
                    BlockPos.asLong(x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ())
            );
        }
    }

    private FluidSection getSection(int x, int y, int z) {
        return FluidSectionManager.getIfAbsent(level, sections, x >> 4, y >> 4, z >> 4);
    }
}
