package com.thepeeingboyairfryers.washwater.common.flow;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import net.minecraft.core.Direction;

public class PseudoRandom {
    private static final Direction[][] randomisedDirectionArrayArray = new Direction[4][4];

    static {
        for (int i = 0; i < 4; i++) {
            int x = i;
            for(Direction dir : Direction.Plane.HORIZONTAL) {
                randomisedDirectionArrayArray[i][x%4] = dir;
                x++;
            }
        }
    }

    public static Direction[] getRandomDirectionArray() {
        return randomisedDirectionArrayArray[(FluidTicker.getCurrentTick() % 4)];
    }
}
