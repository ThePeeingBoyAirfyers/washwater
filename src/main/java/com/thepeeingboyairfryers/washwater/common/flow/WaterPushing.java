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
        BlockPos newPos = origin.relative(direction);
        System.out.println(direction);
        int currentDistance = 0;
        FluidType type = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, type);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemaining(level, newPos, type, volumeToDisplace);
        }

        return volumeToDisplace == 0;
    }

    public static boolean checkIfCanPushWater(ServerLevel level, BlockPos origin,  Direction direction) {
        int maxDistance = maxPistonPushingDistance;
        BlockPos newPos = origin.relative(direction);
        FluidType originType = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int currentDistance = 0;
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, originType);



        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            if (FluidUtil.isSolid(level, newPos) || level.getBlockState(newPos).getFluidState().getFluidType() != originType) {
                //System.out.println("falsed");
                return false;
            }
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemainingImaginary(level, newPos, originType, volumeToDisplace);
        }

        return volumeToDisplace == 0;
    }
}
