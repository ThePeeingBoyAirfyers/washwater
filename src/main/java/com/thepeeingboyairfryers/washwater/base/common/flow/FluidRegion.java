package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public interface FluidRegion {

    default int getFluidVolume(BlockPos pos, FluidType type) {
        return getFluidVolume(pos.getX(), pos.getY(), pos.getZ(), type);
    }

    int getFluidVolume(int x, int y, int z, FluidType type);

    default @NotNull MultiFluidValue getFluids(BlockPos pos) {
        return getFluids(pos.getX(), pos.getY(), pos.getZ());
    }

    @NotNull MultiFluidValue getFluids(int x, int y, int z);

    default int getAllVolume(BlockPos pos) {
        return getAllVolume(pos.getX(), pos.getY(), pos.getZ());
    }

    int getAllVolume(int x, int y, int z);

    default boolean isAir(BlockPos pos) {
        return isAir(pos.getX(), pos.getY(), pos.getZ());
    }

    boolean isAir(int x, int y, int z);

    default boolean isSolid(BlockPos pos) {
        return isSolid(pos.getX(), pos.getY(), pos.getZ());
    }

    boolean isSolid(int x, int y, int z);

    default void setVolume(BlockPos pos, MultiFluidValue fluids) {
        setVolume(pos.getX(), pos.getY(), pos.getZ(), fluids);
    }

    void setVolume(int x, int y, int z, MultiFluidValue fluids);

    @Deprecated
    void setState(int x, int y, int z, BlockState state);
}
