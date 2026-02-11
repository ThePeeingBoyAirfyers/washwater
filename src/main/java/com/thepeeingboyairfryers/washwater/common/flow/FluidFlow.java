package com.thepeeingboyairfryers.washwater.common.flow;

import com.mojang.logging.LogUtils;
import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import org.slf4j.Logger;

public class FluidFlow {
    private FluidFlow() {
        throw new IllegalStateException();
    }

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Direction[] HORIZONTAL_DIRECTIONS =
            new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    public static void tick(FluidRegion region, BlockPos pos) {
        int volume = region.getFluidVolume(pos, WaterInfo.WATER_TYPE);

        if (volume > 0) {
            BlockPos underPos = new BlockPos(pos.getX(), pos.getY() - 1, pos.getZ());
            var underVolume = region.getFluidVolume(underPos, WaterInfo.WATER_TYPE);

            //Downwards flow
            if (!region.isSolid(underPos) && underVolume < WaterInfo.VOLUME_PER_BLOCK) {
                //Delete water at world bottom (void)
                if (underPos.getY() == WaterInfo.MIN_Y) {
                    region.setVolume(pos, water(0));
                    return;
                }
                //Flow down
                var transaction = Math.min(volume, WaterInfo.VOLUME_PER_BLOCK - underVolume);
                region.setVolume(pos, water(volume - transaction));
                region.setVolume(underPos, water(underVolume + transaction));
                volume -= transaction;
                if (volume > 0) {
                    //Flow downwards sideways
                    //equalizeWaterDownwards(region, pos, volume);
                }
            }
            else {
                //Flow sideways
                equalizeWater(region, pos, volume);
            }
        } else {
            LOGGER.warn("Ticking water with no volume");
        }
    }

    public static void equalizeWater(FluidRegion region, BlockPos owner, int volume) {
        if (volume < WaterInfo.SURFACE_TENSION_LIMIT) return;
        int newVolume = volume;

        for (Direction direction : PseudoRandom.getRandomDirectionArray()) {
            int x = owner.getX() + direction.getStepX();
            int y = owner.getY();
            int z = owner.getZ() + direction.getStepZ();
            int otherVolume = region.getFluidVolume(x, y, z, WaterInfo.WATER_TYPE);

            if (region.isSolid(x, y, z)) continue;

            int transfer = (newVolume - otherVolume) / WaterInfo.FLOW_DIVIDER;
            if (transfer > 2 || transfer < -2) {
                newVolume -= transfer;
                region.setVolume(x, y, z, water(otherVolume + transfer));
            }
        }

        if (newVolume != volume) region.setVolume(owner, water(newVolume));
    }

    public static void equalizeWaterDownwards(FluidRegion region, BlockPos owner, int volume) {
        if (volume < WaterInfo.SURFACE_TENSION_LIMIT) return;
        int newVolume = volume;

        for (Direction direction : PseudoRandom.getRandomDirectionArray()) {
            int x = owner.getX() + direction.getStepX();
            int y = owner.getY() - 1;
            int z = owner.getZ() + direction.getStepZ();
            int otherVolume = region.getFluidVolume(x, y, z, WaterInfo.WATER_TYPE);

            if (otherVolume < 0) continue;

            int transfer = Math.min(newVolume, WaterInfo.VOLUME_PER_BLOCK - otherVolume);
            if (transfer > 2 || transfer < -2) {
                newVolume -= transfer;
                region.setVolume(x, y, z, water(otherVolume + transfer));
            }

            if (volume == 0) break;
        }

        if (newVolume != volume) region.setVolume(owner, water(newVolume));
    }

    private static MultiFluidValue water(int i) {
        return MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) i);
    }

}
