package com.thepeeingboyairfryers.washwater.common.util;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.component.WWDataComponentTypes;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.item.PrecisionBucketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.Queue;

public class BucketBfs {

    private BucketBfs() { }

    private static final int RADIUS = WaterInfo.PRECISION_BUCKET_RADIUS;

    public static void runBucketBFS(Level iLevel, BlockPos iPos, ItemStack iStack, Player iPlayer) {
        Level level = iLevel;
        BlockPos pos = iPos;
        int xX = pos.getX() - RADIUS;
        int zZ = pos.getZ() - RADIUS;

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(RADIUS, RADIUS, 0));
        while (!queue.isEmpty()) {  
            Node popped = queue.poll();
            BlockPos nodePos = getAbsolutePos(xX, zZ, pos, popped, 0, 0);
            if (!isBlockWater(level, nodePos) || popped.distance > RADIUS) {
                continue;
            }
            if (precisionBucketPickup(level, nodePos, iStack, iPlayer)) {
                return;
            }
            addNeighbours(popped, queue);
        }
    }

    public static boolean precisionBucketPickup(Level level, BlockPos targetPos, ItemStack itemStack, Player player) {
        int fillLevel = 0;
        if (itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL) != null) {
            fillLevel = itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL);
        }
        if (fillLevel == WaterInfo.PRECISION_BUCKET_CAPACITY)
            return true;

        int bucketRemainingSpace = WaterInfo.PRECISION_BUCKET_CAPACITY - fillLevel;
        if (!level.isClientSide) {
            short oldVolume = FluidUtil.getVolume(level, targetPos, WaterInfo.WATER_TYPE);
            int newVolume = 0;
            int newBucketFillLevel;
            if (oldVolume > bucketRemainingSpace) {
                newVolume = oldVolume - bucketRemainingSpace;
                newBucketFillLevel = WaterInfo.PRECISION_BUCKET_CAPACITY;
            } else {
                newBucketFillLevel = fillLevel + oldVolume;
            }
            if (newVolume > 0) {
                FluidUtil.setVolume((ServerLevel) level, targetPos, MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume));
            } else {
                FluidUtil.setVolume((ServerLevel) level, targetPos, MultiFluidValue.single(WaterInfo.WATER_TYPE, (short) newVolume));
            }
            itemStack.set(WWDataComponentTypes.BUCKET_FILL_LEVEL, newBucketFillLevel);
            return newBucketFillLevel == WaterInfo.PRECISION_BUCKET_CAPACITY;
        } else {
            return true;
        }
    }

    private static void addNeighbours(Node popped, Queue<Node> queue) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int xDist = Math.abs(popped.x + dir.getStepX() - RADIUS);
            int zDist = Math.abs(popped.z + dir.getStepZ() - RADIUS);
            if (xDist <= RADIUS && zDist <= RADIUS) {
                queue.add(new Node(popped.x + dir.getStepX(), popped.z + dir.getStepZ(), popped.distance + 1));
            }
        }
    }

    private static boolean isBlockWater(Level level, BlockPos iPos) {
        return FluidUtil.getVolume(level, iPos, FluidUtil.WATER_TYPE) > 0;
    }

    private static BlockPos getAbsolutePos(int xX, int zZ, BlockPos pos, Node popped, int offsetX, int offsetZ) {
        return new BlockPos(xX + popped.x + offsetX, pos.getY(), zZ + popped.z + offsetZ);
    }

    public static class Node {
        private int x;
        private int z;
        private int distance;

        public Node(int iX, int iZ, int iDistance) {
            this.x = iX;
            this.z = iZ;
            this.distance = iDistance;
        }
    }
}
