package com.thepeeingboyairfryers.washwater.common;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class WaterInfo {
    public static final FluidType WATER_TYPE = NeoForgeMod.WATER_TYPE.value();
    public static short volumePerBlock = 1000;
    public static short volumePerLevel = (short) ((volumePerBlock / 8) + 1);
    public static short cutOffValue = (short) (volumePerLevel * 7);
    public static short surfaceTensionLimit = 20;
    public static int flowDivider = 8;
    public static int minY = -64;
    public static short getWaterVolumeOfState(BlockState state) {
        FluidState fluidstate = state.getFluidState();
        if (fluidstate.isEmpty()) {
            if (state.isAir() || !state.isSolid())
                return 0;
            else
                return -1;
        } else return (short) (fluidstate.getAmount() * volumePerLevel);
    }

    public static FluidState getWaterState(int value) {
        if (value <= 0) return Fluids.EMPTY.defaultFluidState();
        return Fluids.WATER.defaultFluidState();
    }

    public static float getHeight(int volume) {
        if (volume < 0) return 0;
        return ((float) volume) / volumePerBlock;
    }
}
