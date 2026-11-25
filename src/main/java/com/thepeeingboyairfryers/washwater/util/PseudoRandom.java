package com.thepeeingboyairfryers.washwater.util;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import net.minecraft.core.Direction;

public class PseudoRandom {
    private static final Direction[][] RANDOMISED_DIRECTION_ARRAY_ARRAY = new Direction[4][4];
    private static final Direction[][] RANDOMISED_3D_DIRECTION_ARRAY_ARRAY = new Direction[6][6];

    static {
        for (int i = 0; i < 4; i++) {
            int x = i;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                RANDOMISED_DIRECTION_ARRAY_ARRAY[i][x % 4] = dir;
                x++;
            }
        }
    }

    static {
        for (int i = 0; i < 6; i++) {
            int x = i;
            for (Direction dir : Direction.values()) {
                RANDOMISED_3D_DIRECTION_ARRAY_ARRAY[i][x % 6] = dir;
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

    public static Direction[] getRandom3DDirectionArray() {
        return RANDOMISED_3D_DIRECTION_ARRAY_ARRAY[(FluidTicker.getCurrentTick() % 6)];
    }
}
