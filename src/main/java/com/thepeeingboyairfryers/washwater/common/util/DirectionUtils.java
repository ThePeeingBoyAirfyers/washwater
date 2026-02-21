package com.thepeeingboyairfryers.washwater.common.util;

import net.minecraft.core.Direction;

public class DirectionUtils {
    public static final Direction[] HORIZONTAL_TOP =
            new Direction[]{Direction.UP, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.WEST};

    private DirectionUtils() {
        throw new IllegalStateException("Utility class");
    }

}
