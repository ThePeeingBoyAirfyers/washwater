package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidType;

public class WaterPushing {


    //GET FLUID LEVEL
    // FluidUtil.getVolume(level, newPos, fluidType);
    //SET FLUID LEVEL
    // FluidUtil.setVolume(level, newPos, MultiFluidValue.single(fluidType, (short) newVolume));

    static int maxPistonPushingDistance = 8;
    public static boolean tryPushWater(ServerLevel level, BlockPos origin, Direction direction) {
        int maxDistance = maxPistonPushingDistance;
        BlockPos firstPos = origin.relative(direction);
        int currentDistance = 0;
        FluidType type = level.getBlockState(firstPos).getFluidState().getType().getFluidType();
        int volumeToDisplace = FluidUtil.getVolume(level, firstPos, type);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            firstPos = firstPos.relative(direction);
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemaining(level, firstPos, type, volumeToDisplace);
        }

        return volumeToDisplace == 0;
    }

    public static boolean checkIfCanPushWater(ServerLevel level, BlockPos origin, FluidType type,  Direction direction) {
        int maxDistance = maxPistonPushingDistance;
        BlockPos newPos = origin.relative(direction);
        FluidType fluidType = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int currentDistance = 0;
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, fluidType);



        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            if (FluidUtil.getVolume(level, newPos, fluidType) < 0) {
                return false;
            }
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemainingImaginary(level, newPos, type, volumeToDisplace);
        }

        return volumeToDisplace == 0;
    }

    //UTIL METHODS MAY BE MOVED LATER

    public static int addWaterLevelAndReturnRemainingImaginary(BlockPos pos, int WaterLevel, ServerLevel level) {
        int oldWaterLevel = getWaterLevel(pos, level);
        if (oldWaterLevel < 0) {
            //WaterMod.LOGGER.warn("Tried to add water WaterLevel to a non-air block");
            return WaterLevel;
        }

        int remainder;
        int newWaterLevel = oldWaterLevel + WaterLevel;
        if (newWaterLevel > 8) {
            //setWaterLevel(level, pos, WaterInfo.WaterLevelPerBlock);
            remainder = addWaterLevelAndReturnRemainingImaginary(pos.above(), newWaterLevel - 8, level);
        } else {
            remainder = 0;
            //setWaterLevel(level, pos, newWaterLevel);
        }
        return remainder;
    }
}
