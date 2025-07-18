package com.thepeeingboyairfryers.washwater.common.flow;

import com.mojang.logging.LogUtils;
import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.slf4j.Logger;

public class FluidFlow {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static void tick(FluidRegion region, BlockPos pos) {
        int volume = region.getFluidVolume(pos, WaterInfo.WATER_TYPE);

        if (volume > 0) {


            //Flow down
            var underVolume = region.getFluidVolume(pos.getX(), pos.getY() - 1 , pos.getZ(), WaterInfo.WATER_TYPE);
            if (pos.getY() -1 == WaterInfo.minY && underVolume == 0) {
                //Delete water
                    region.setVolume(pos, WaterInfo.WATER_TYPE, 0);
            }
            else {
                if (underVolume >= 0 && underVolume < WaterInfo.volumePerBlock) {
                    var transaction = Math.min(volume, WaterInfo.volumePerBlock - underVolume);
                    region.setVolume(pos, WaterInfo.WATER_TYPE, volume - transaction);
                    region.setVolume(pos.getX(), pos.getY() - 1, pos.getZ(), WaterInfo.WATER_TYPE, underVolume + transaction);

                    volume -= transaction;

                    if (volume > 0) {
                        //Flow downwards sideways
                        equalizeWaterDownwards(region, pos, volume);
                    }
                } else {
                    //If under is solid or filled up then flow to sides
                    equalizeWater(region, pos, volume);
                }
            }



        } else {
            LOGGER.warn("Ticking water with no volume");
        }
    }


    public static void equalizeWater(FluidRegion region, BlockPos owner, int volume) {
        if (volume < WaterInfo.surfaceTensionLimit) return;
        int newVolume = volume;

        for (Direction direction : PseudoRandom.getRandomDirectionArray()) {
            int x = owner.getX() + direction.getStepX();
            int y = owner.getY();
            int z = owner.getZ() + direction.getStepZ();
            int otherVolume = region.getFluidVolume(x, y, z, WaterInfo.WATER_TYPE);

            if (otherVolume < 0) continue;

            int transfer = (newVolume - otherVolume) / WaterInfo.flowDivider;
            if (transfer > 2 || transfer < -2) {
                newVolume -= transfer;
                region.setVolume(x, y, z, WaterInfo.WATER_TYPE, otherVolume + transfer);
            }
        }

        if (newVolume != volume) region.setVolume(owner, WaterInfo.WATER_TYPE, newVolume);
    }

    public static void equalizeWaterDownwards(FluidRegion region, BlockPos owner, int volume) {
        if (volume < WaterInfo.surfaceTensionLimit) return;
        int newVolume = volume;

        for (Direction direction : PseudoRandom.getRandomDirectionArray()) {
            int x = owner.getX() + direction.getStepX();
            int y = owner.getY() - 1;
            int z = owner.getZ() + direction.getStepZ();
            int otherVolume = region.getFluidVolume(x, y, z, WaterInfo.WATER_TYPE);

            if (otherVolume < 0) continue;

            int transfer = Math.min(newVolume, WaterInfo.volumePerBlock - otherVolume);
            if (transfer > 2 || transfer < -2) {
                newVolume -= transfer;
                region.setVolume(x, y, z, WaterInfo.WATER_TYPE, otherVolume + transfer);
            }

            if (volume == 0) break;
        }

        if (newVolume != volume) region.setVolume(owner, WaterInfo.WATER_TYPE, newVolume);
    }
}
