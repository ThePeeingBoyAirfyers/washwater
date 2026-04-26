package com.thepeeingboyairfryers.washwater.common.util;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.item.PrecisionBucketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.Queue;

public class BucketBfs {

    private BucketBfs() { }

    private static final int RADIUS = WaterInfo.PRECISION_BUCKET_RADIUS;
    private static int xX;
    private static int zZ;
    private static Level level;
    private static BlockPos pos;

    public static void runBucketBFS(Level iLevel, BlockPos iPos, ItemStack iStack, Player iPlayer) {
        level = iLevel;
        pos = iPos;
        xX = pos.getX() - RADIUS;
        zZ = pos.getZ() - RADIUS;

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(RADIUS, RADIUS, 0));
        while (!queue.isEmpty()) {
            Node popped = queue.poll();
            BlockPos nodePos = getAbsolutePos(popped, 0, 0);
            if (!isBlockWater(nodePos) || popped.distance > RADIUS) {
                continue;
            }
            if (PrecisionBucketItem.precisionBucketPickup(level, nodePos, iStack, iPlayer))
                return;
            addNeighbours(popped, queue);

        }
    }

    private static void addNeighbours(Node popped, Queue<Node> queue) {
        int diameter = 2 * RADIUS + 1;
        if ((popped.x - 1 >= 0 && popped.x - 1 < diameter)) {
            queue.add(new Node(popped.x - 1, popped.y, popped.distance + 1));
        }
        if ((popped.x + 1 >= 0 && popped.x + 1 < diameter)) {
            queue.add(new Node(popped.x + 1, popped.y, popped.distance + 1));
        }
        if ((popped.y - 1 >= 0 && popped.y - 1 < diameter)) {
            queue.add(new Node(popped.x, popped.y - 1, popped.distance + 1));
        }
        if ((popped.y + 1 >= 0 && popped.y + 1 < diameter)) {
            queue.add(new Node(popped.x, popped.y + 1, popped.distance + 1));
        }
    }

    private static boolean isBlockWater(BlockPos iPos) {
        return FluidUtil.getVolume(level, iPos, FluidUtil.WATER_TYPE) > 0;
    }

    private static BlockPos getAbsolutePos(Node popped, int offsetX, int offsetZ) {
        return new BlockPos(xX + popped.x + offsetX, pos.getY(), zZ + popped.y + offsetZ);
    }

    public static class Node {
        private int x;
        private int y;
        private int distance;

        public Node(int iX, int iY, int iDistance) {
            this.x = iX;
            this.y = iY;
            this.distance = iDistance;
        }
    }
}
