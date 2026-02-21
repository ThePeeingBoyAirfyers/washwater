package com.thepeeingboyairfryers.washwater.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class WaterInfo {
    public static final FluidType WATER_TYPE = NeoForgeMod.WATER_TYPE.value();
    public static final short VOLUME_PER_BLOCK = 1000;
    public static final short VOLUME_PER_LEVEL = (short) ((VOLUME_PER_BLOCK / 8) + 1);
    public static final short CUT_OFF_VALUE = (short) (VOLUME_PER_LEVEL * 7);
    public static final short SURFACE_TENSION_LIMIT = 20;
    public static final int FLOW_DIVIDER = 8;
    public static final int MIN_Y = -64;
    private WaterInfo() {
        throw new IllegalStateException();
    }

    public static short getWaterVolumeOfState(BlockState state) {
        FluidState fluidstate = state.getFluidState();
        if (fluidstate.isEmpty()) {
            if (state.isAir() || !state.isSolid())
                return 0;
            else
                return -1;
        } else return (short) (fluidstate.getAmount() * VOLUME_PER_LEVEL);
    }

    public static FluidState getWaterState(int value) {
        if (value <= 0) return Fluids.EMPTY.defaultFluidState();
        return Fluids.WATER.defaultFluidState();
    }

    public static float getHeight(int volume) {
        if (volume < 0) return 0;
        return ((float) volume) / VOLUME_PER_BLOCK;
    }
}
