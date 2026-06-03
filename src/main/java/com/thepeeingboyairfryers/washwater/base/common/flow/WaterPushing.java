package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;

public class WaterPushing {

    private WaterPushing() { }

    public static boolean tryPushWater(ServerLevel level, BlockPos origin, Direction direction) {
        int maxDistance = WaterInfo.MAX_PUSHING_DISTANCE;
        BlockPos newPos = origin.relative(direction);
        int currentDistance = 0;
        FluidType type = level.getBlockState(newPos).getFluidState().getType().getFluidType();
        int volumeToDisplace = FluidUtil.getVolume(level, newPos, type);

        while (volumeToDisplace > 0 && currentDistance < maxDistance) {
            newPos = newPos.relative(direction);
            currentDistance++;
            volumeToDisplace = (volumeToDisplace - FluidUtil.addVolume(level, newPos, type, volumeToDisplace));
        }

        return volumeToDisplace == 0;
    }

    public static boolean checkIfCanPushWater(ServerLevel level, BlockPos origin,  Direction direction) {
        int maxDistance = WaterInfo.MAX_PUSHING_DISTANCE;
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
            volumeToDisplace = (volumeToDisplace - FluidUtil.canAddVolume(level, newPos, originType, volumeToDisplace));
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
            return (FluidUtil.addVolume(level, pos, type, availableVolume) == availableVolume);
        } else {
            if (availableVolume > 0) {
                int i = 0;
                for (Direction dir : viableHorDirections) {
                    int cut = initialVolume / viableHorDirections.size();
                    if (i == 0)
                        cut += availableVolume % viableHorDirections.size();
                    int remainder = (cut - FluidUtil.addVolume(level, pos.relative(dir).mutable(), type, cut));
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
            return (FluidUtil.canAddVolume(level, pos, type, availableVolume) == availableVolume);
        } else {
            if (availableVolume > 0) {
                int i = 0;
                for (Direction dir : viableHorDirections) {
                    int cut = initialVolume / viableHorDirections.size();
                    if (i == 0)
                        cut += availableVolume % viableHorDirections.size();

                    int remainder = (cut - FluidUtil.canAddVolume(level, pos.relative(dir).mutable(), type, cut));
                    availableVolume = availableVolume - cut + remainder;
                    i++;
                }
            }
        }
        return availableVolume == 0;
    }

}
