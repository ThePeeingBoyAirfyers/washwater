package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public abstract class CachedFluidRegion implements FluidRegion {
    private int cachedX = Integer.MIN_VALUE, cachedY = Integer.MIN_VALUE, cachedZ = Integer.MIN_VALUE;
    private MultiFluidValue cachedFluid;

    @Override
    public int getFluidVolume(int x, int y, int z, FluidType type) {
        return getFluids(x, y, z).forFluid(type);
    }

    @Override
    public @NotNull MultiFluidValue getFluids(int x, int y, int z) {
        if (cachedX == x && cachedY == y && cachedZ == z) return cachedFluid;
        cachedX = x;
        cachedY = y;
        cachedZ = z;
        cachedFluid = getFluidSection(x >> 4, y >> 4, z >> 4).getFluids(x & 15, y & 15, z & 15);
        return cachedFluid;
    }

    @Override
    public int getAllVolume(int x, int y, int z) {
        return getFluids(x, y, z).getTotalVolume();
    }

    @Override
    public boolean isAir(int x, int y, int z) {
        return getFluids(x, y, z).isEmpty();
    }

    @Override
    public boolean isSolid(int x, int y, int z) {
        BlockState localBS = getBlockSection(x >> 4, y >> 4, z >> 4).getBlockState(x & 15, y & 15, z & 15);
        return (!localBS.isAir() && localBS.getFluidState().isEmpty());
    }

    @Override
    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        getFluidSection(x >> 4, y >> 4, z >> 4).setVolume(x & 15, y & 15, z & 15, value);
        cachedX = x;
        cachedY = y;
        cachedZ = z;
        cachedFluid = value;

        if (!value.isEmpty()) {
            toBeTicked(x, y, z);
        } else {
            toBeUnticked(x, y, z);
        }

        for (Direction direction : Direction.values()) {
            int xD = x + direction.getStepX();
            int yD = y + direction.getStepY();
            int zD = z + direction.getStepZ();
            if (isAir(xD, yD, zD)) {
                toBeUnticked(xD, yD, zD);
            } else {
                toBeTicked(xD, yD, zD);
            }
        }
    }

    protected abstract void toBeTicked(int x, int y, int z);

    protected abstract void toBeUnticked(int x, int y, int z);

    protected abstract LevelChunkSection getBlockSection(int x, int y, int z);

    protected abstract FluidSection getFluidSection(int x, int y, int z);
}
