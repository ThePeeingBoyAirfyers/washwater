package com.thepeeingboyairfryers.washwater.common.util;

import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import net.minecraft.core.Direction;

public class PseudoRandom {
    private static final Direction[][] RANDOMISED_DIRECTION_ARRAY_ARRAY = new Direction[4][4];

    static {
        for (int i = 0; i < 4; i++) {
            int x = i;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                RANDOMISED_DIRECTION_ARRAY_ARRAY[i][x % 4] = dir;
                x++;
            }
        }
    }

    private PseudoRandom() {
        throw new IllegalStateException("Utility class");
    }

    public static Direction[] getRandomDirectionArray() {
        return RANDOMISED_DIRECTION_ARRAY_ARRAY[(FluidTicker.getCurrentTick() % 4)];
    }
}
