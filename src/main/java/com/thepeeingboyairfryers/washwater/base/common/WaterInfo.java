package com.thepeeingboyairfryers.washwater.base.common;

import com.thepeeingboyairfryers.washwater.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class WaterInfo {
    public static final FluidType WATER_TYPE = NeoForgeMod.WATER_TYPE.value();
    public static final short VOLUME_PER_BLOCK = (short) Config.VOLUME_PER_BLOCK.getAsInt();
    public static final short PRECISION_BUCKET_CAPACITY = (short) Config.PRECISION_BUCKET_CAPACITY.getAsInt();
    public static final short VOLUME_PER_LEVEL = (short) ((VOLUME_PER_BLOCK / 8) + 1);
    public static final short CUT_OFF_VALUE = (short) (VOLUME_PER_LEVEL * 7);
    public static final short SURFACE_TENSION_LIMIT = 20;
    public static final int FLOW_DIVIDER = 8;
    @Deprecated public static final int MIN_Y = -64;
    public static final int PRECISION_BUCKET_RADIUS = 2;

    private static final BlockPos.MutableBlockPos SOLID_POS = new BlockPos.MutableBlockPos();
    public static boolean isSolid(BlockGetter getter, BlockState state, int x, int y, int z) {
        if (!Config.EXPERIMENTAL_PARTIAL_BLOCKS.getAsBoolean())
            return !state.isAir();

        // TODO make it work better, disabled by default for now
        VoxelShape shape = state.getCollisionShape(getter, SOLID_POS.set(x, y, z));
        if (shape.isEmpty()) return false;
        AABB aabb = shape.bounds();
        return aabb.maxX >= 0.99 && aabb.maxY >= 0.99 && aabb.maxZ >= 0.99
                && aabb.minX <= 0.01 && aabb.minY <= 0.01 && aabb.minZ <= 0.01;
    }

    private WaterInfo() {
        throw new IllegalStateException();
    }
}
