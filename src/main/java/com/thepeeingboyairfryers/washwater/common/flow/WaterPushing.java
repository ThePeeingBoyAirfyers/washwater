package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;

public class WaterPushing {

    private WaterPushing() { }

    public static boolean tryPushWater(ServerLevel level, BlockPos origin, Direction direction) {
        int maxDistance = WaterInfo.MAX_PISTON_PUSHING_DISTANCE;
        BlockPos newPos = origin.relative(direction);
        System.out.println(direction);
        int currentDistance = 0;
        FluidType type = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, type);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemaining(level, newPos, type, volumeToDisplace, true, true);
        }

        return volumeToDisplace == 0;
    }

    public static boolean checkIfCanPushWater(ServerLevel level, BlockPos origin,  Direction direction) {
        int maxDistance = WaterInfo.MAX_PISTON_PUSHING_DISTANCE;
        BlockPos newPos = origin.relative(direction);
        FluidType originType = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int currentDistance = 0;
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, originType);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            if (FluidUtil.isSolid(level, newPos) || level.getBlockState(newPos).getFluidState().getFluidType() != originType) {
                return false;
            }
            currentDistance++;
            volumeToDisplace = FluidUtil.addWaterVolumeAndReturnRemaining(level, newPos, originType, volumeToDisplace, true, false);
        }

        return volumeToDisplace == 0;
    }

    public static boolean displaceFluids(ServerLevel level, BlockPos pos) {
        FluidType type = level.getFluidState(pos).getFluidType();
        int availableVolume = FluidUtil.getVolume(level, pos, type);
        int initialVolume = availableVolume;
        ArrayList<Direction> viableHorDirections = new ArrayList<>(0);

        for (Direction dir : PseudoRandom.getRandomDirectionArray()) {
            if (!FluidUtil.isSolid(level, pos.relative(dir)) && !FluidUtil.isFilledUp(level, pos.relative(dir))) {
                viableHorDirections.add(dir);
            }
        }

        if (viableHorDirections.isEmpty()) {
            return FluidUtil.addVolume(level, pos, type, availableVolume);
        }
        else {
            if (availableVolume > 0) {
                int i = 0;
                for (Direction dir : viableHorDirections) {
                    int cut = initialVolume / viableHorDirections.size();
                    if (i == 0)
                        cut += availableVolume % viableHorDirections.size();
                    int remainder = FluidUtil.addWaterVolumeAndReturnRemaining(level, pos.relative(dir).mutable(), type, cut, true, true);
                    availableVolume = availableVolume - cut + remainder;
                    i++;
                }
            }
        }
        return availableVolume == 0;
    }

    public static boolean checkIfCanDisplaceFluids(Level level, BlockPos pos) {
        FluidType type = level.getFluidState(pos).getFluidType();
        int availableVolume = FluidUtil.getVolume(level, pos, type);
        int initialVolume = availableVolume;
        ArrayList<Direction> viableHorDirections = new ArrayList<>(0);

        for (Direction dir : PseudoRandom.getRandomDirectionArray()) {
            if (!FluidUtil.isSolid(level, pos.relative(dir)) && !FluidUtil.isFilledUp(level, pos.relative(dir))) {
                viableHorDirections.add(dir);
            }
        }

        if (viableHorDirections.isEmpty()) {
            return FluidUtil.canAddVolume(level, pos, type, availableVolume);
        }
        else {
            if (availableVolume > 0) {
                int i = 0;
                for (Direction dir : viableHorDirections) {
                    int cut = initialVolume / viableHorDirections.size();
                    if (i == 0)
                        cut += availableVolume % viableHorDirections.size();

                    int remainder = FluidUtil.addWaterVolumeAndReturnRemaining(level, pos.relative(dir).mutable(), type, cut, true, false);
                    availableVolume = availableVolume - cut + remainder;
                    i++;
                }
            }
        }
        return availableVolume == 0;
    }

}
