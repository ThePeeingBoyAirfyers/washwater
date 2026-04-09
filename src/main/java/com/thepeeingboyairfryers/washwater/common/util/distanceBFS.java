package com.thepeeingboyairfryers.washwater.common.util;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.item.PrecisionBucketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.Queue;

public class distanceBFS {

    public static final int BFSRadius = 2;
    static int BFSDiameter = 2 * BFSRadius + 1;
    static int xX;
    static int zZ;
    static Player player;
    static ItemStack stack;
    static Level level;
    static BlockPos pos;

    public static void runBucketBFS(Level iLevel, BlockPos iPos, ItemStack iStack, Player iPlayer) {
        level = iLevel;
        pos = iPos;
        player = iPlayer;
        stack = iStack;
        xX = pos.getX() - BFSRadius;
        zZ = pos.getZ() - BFSRadius;

        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(BFSRadius, BFSRadius, 0));
        while (!queue.isEmpty()) {
            Node popped = queue.poll();
            BlockPos nodePos = getAbsolutePos(popped, 0, 0);
            if (!isBlockWater(nodePos) || popped.distance > BFSRadius) {
                continue;
            }
            if (PrecisionBucketItem.precisionBucketPickup(level, nodePos, stack, player))
                return;
            addNeighbours(popped, queue);

        }
    }

    private static void addNeighbours(Node popped, Queue<Node> queue) {
        if ((popped.x - 1 >= 0 && popped.x - 1 < BFSDiameter)) {
            queue.add(new Node(popped.x - 1, popped.y, popped.distance + 1));
        }
        if ((popped.x + 1 >= 0 && popped.x + 1 < BFSDiameter)) {
            queue.add(new Node(popped.x + 1, popped.y, popped.distance + 1));
        }
        if ((popped.y - 1 >= 0 && popped.y - 1 < BFSDiameter)) {
            queue.add(new Node(popped.x, popped.y - 1, popped.distance + 1));
        }
        if ((popped.y + 1 >= 0 && popped.y + 1 < BFSDiameter)) {
            queue.add(new Node(popped.x, popped.y + 1, popped.distance + 1));
        }
    }

    private static boolean isBlockWater(BlockPos pos) {
        return FluidUtil.getVolume(level, pos, FluidUtil.WATER_TYPE) > 0;
    }

    private static BlockPos getAbsolutePos(Node popped, int offsetX, int offsetZ) {
        return new BlockPos(xX + popped.x + offsetX, pos.getY(), zZ + popped.y + offsetZ);
    }

    public static class Node {
        int x;
        int y;
        int distance;

        public Node(int x, int y, int distance) {
            this.x = x;
            this.y = y;
            this.distance = distance;
        }
    }
}
