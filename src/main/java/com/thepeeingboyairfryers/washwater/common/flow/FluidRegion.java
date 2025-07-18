package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.util.MultiFluidResult;
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public interface FluidRegion {

    default int getFluidVolume(BlockPos pos, FluidType type) {
        return getFluidVolume(pos.getX(), pos.getY(), pos.getZ(), type);
    }

    int getFluidVolume(int x, int y, int z, FluidType type);

    default MultiFluidResult getFluids(BlockPos pos) {
        return getFluids(pos.getX(), pos.getY(), pos.getZ());
    }

    MultiFluidResult getFluids(int x, int y, int z);

    default boolean isAir(BlockPos pos) {
        return isAir(pos.getX(), pos.getY(), pos.getZ());
    }
    boolean isAir(int x, int y, int z);

    default boolean isWater(BlockPos pos) {
        return isWater(pos.getX(), pos.getY(), pos.getZ());
    }
    default boolean isWater(int x, int y, int z) {
        return getFluidVolume(x, y, z, NeoForgeMod.WATER_TYPE.value()) > 0;
    }

    default boolean isSolid(BlockPos pos) {
        return isSolid(pos.getX(), pos.getY(), pos.getZ());
    }
    boolean isSolid(int x, int y, int z);

    default void setVolume(BlockPos pos, FluidType type, int volume) {
        if (volume > WaterInfo.volumePerBlock) {
            setVolume(pos, type, WaterInfo.volumePerBlock);
            WashWater.LOGGER.warn("Tried to set water volume higher than max");
            return;
        }

        setVolume(pos.getX(), pos.getY(), pos.getZ(), type, volume);
    }

    void setVolume(int x, int y, int z, FluidType type, int volume);

}
