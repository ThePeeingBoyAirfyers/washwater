package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;

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
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemaining(level, newPos, type, volumeToDisplace, true);
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

    //TODO Fix Ewoud not making addFluid return a bool (ability to fail)
    public static boolean displaceFluids(ServerLevel level, BlockPos pos) {
        FluidType type = level.getFluidState(pos).getFluidType();
        int availableVolume = FluidUtil.getVolume(level, pos, type);
        ArrayList<Direction> viableHorDirections = new ArrayList<>(0);
        int maxLoops = 10;

        boolean result = false;
        for (Direction dir : PseudoRandom.getRandomDirectionArray()) {
            if (!FluidUtil.isSolid(level, pos.relative(dir)) && !FluidUtil.isFilledUp(level, pos.relative(dir))) {
                viableHorDirections.add(dir);
            }
        }

        if (viableHorDirections.isEmpty()) {
            if (!FluidUtil.isSolid(level, pos.above())) {
                //TODO Here
                //success = (FluidUtil.addVolume(level, pos, availableVolume);
                FluidUtil.addVolume(level, pos, type, availableVolume);
                return result;
            }
        }
        else {
            BlockPos.MutableBlockPos scratchPos = pos.mutable();
            int i = 0;
            while (availableVolume > 0 && i <= maxLoops) {
                int cut = availableVolume / viableHorDirections.size();
                for (Direction dir : viableHorDirections) {
                    scratchPos = pos.relative(dir).mutable();
                        //if (FluidUtil.addVolume(level, pos.relative(dir), type, stepSize))
                        //availableVolume - stepSize
                        int remainder = FluidUtil.addWaterVolumeAndReturnRemaining(level, scratchPos, type, cut, false);
                        availableVolume = availableVolume - cut + remainder;
                }
                i++;
            }
        }
        //System.out.println("available " + availableVolume);
        result = availableVolume <= 4;
        System.out.println("result: " + result);
        return result;
    }

    //TODO Fix Ewoud not making addFluid return a bool (ability to fail)
    public static boolean checkIfCanDisplaceFluids(ServerLevel level, BlockPos pos) {
        FluidType type = level.getFluidState(pos).getFluidType();
        int availableVolume = FluidUtil.getVolume(level, pos, type);
        ArrayList<Direction> viableHorDirections = new ArrayList<>(0);
        int maxLoops = 10;

        boolean result = false;
        for (Direction dir : PseudoRandom.getRandomDirectionArray()) {
            if (!FluidUtil.isSolid(level, pos.relative(dir)) && !FluidUtil.isFilledUp(level, pos.relative(dir))) {
                viableHorDirections.add(dir);
            }
        }

        if (viableHorDirections.isEmpty()) {
            if (!FluidUtil.isSolid(level, pos.above())) {
                //TODO Here
                //success = (FluidUtil.addVolume(level, pos, availableVolume);
                FluidUtil.addVolume(level, pos, type, availableVolume);
                return result;
            }
        }
        else {
            BlockPos.MutableBlockPos scratchPos = pos.mutable();
            int i = 0;
            while (availableVolume > 0 && i <= maxLoops) {
                int cut = availableVolume / viableHorDirections.size();
                for (Direction dir : viableHorDirections) {
                    scratchPos = pos.relative(dir).mutable();
                    //if (FluidUtil.addVolume(level, pos.relative(dir), type, stepSize))
                    //availableVolume - stepSize
                    int remainder = FluidUtil.addWaterVolumeAndReturnRemainingImaginary(level, scratchPos, type, cut, false);
                    availableVolume = availableVolume - cut + remainder;
                }
                i++;
            }
        }
        //System.out.println("available " + availableVolume);
        result = availableVolume <= 4;
        System.out.println("result: " + result);
        return result;
    }

/*            else {
        BlockPos.MutableBlockPos scratchPos = pos.mutable();
        int cut = availableVolume / viableHorDirections.size();
        while (availableVolume > 0) {
            for (Direction dir : viableHorDirections) {
                scratchPos.move(dir);
                //if (FluidUtil.addVolume(level, pos.relative(dir), type, stepSize))
                //availableVolume - stepSize
                FluidUtil.addVolume(level, pos.relative(dir), type, cut);
                availableVolume -= cut;


            }
        }
    }*/
}
