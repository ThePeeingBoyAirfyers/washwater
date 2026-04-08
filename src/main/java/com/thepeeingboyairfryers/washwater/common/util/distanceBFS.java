package com.thepeeingboyairfryers.washwater.common.util;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.LinkedList;
import java.util.Queue;

public class distanceBFS {

    public static int BFSRadius = 2;
    static int BFSDiameter = 2 * BFSRadius;
    static int xX;
    static int zZ;
    private static int[][] matrix = new int[BFSDiameter][BFSDiameter];


    public static int[][] fillWorldBFSMatrix(BlockPos center, Level level) {

        xX = center.getX() - BFSRadius;
        zZ = center.getZ() - BFSRadius;

        for (int iX = 0; iX < BFSDiameter; iX++) {
            for (int iZ = 0; iZ < BFSDiameter; iZ++) {
                BlockPos internalPos = new BlockPos(iX + xX, center.getY(), iZ + zZ);
                matrix[iX][iZ] = FluidUtil.getVolume(level, internalPos, FluidUtil.WATER_TYPE) == 0 ? -1 : 0;
            }
        }
        matrix[BFSRadius][BFSRadius] = -2;
        return matrix;
    }

    public static int[][] runDistanceMapBFS(BlockPos pos, Level level) {
        Queue<Node> queue = new LinkedList<>();
        queue.add(new Node(pos.getX(), pos.getZ(), 0));
        matrix = fillWorldBFSMatrix(pos, level);
        while (!queue.isEmpty()) {
            Node popped = queue.poll();

                if (matrix[popped.x][popped.y] > popped.distance) {
                    matrix[popped.x][popped.y] = popped.distance;
                }
                addNeighbours(popped, matrix, queue);
            }
        return matrix;
    }

    private static void addNeighbours(Node popped, int[][] matrix, Queue<Node> queue) {
        if ((popped.x - 1 >= 0 && popped.x - 1 < matrix.length) && matrix[popped.x - 1][popped.y] != -1 && matrix[popped.x - 1][popped.y] > popped.distance + 1) {
            queue.add(new Node(popped.x - 1, popped.y, popped.distance + 1));
        }
        if ((popped.x + 1 >= 0 && popped.x + 1 < matrix.length) && matrix[popped.x + 1][popped.y] != -1 && matrix[popped.x + 1][popped.y] > popped.distance + 1) {
            queue.add(new Node(popped.x + 1, popped.y, popped.distance + 1));
        }
        if ((popped.y - 1 >= 0 && popped.y - 1 < matrix.length) && matrix[popped.x][popped.y - 1] != -1 && matrix[popped.x][popped.y - 1] > popped.distance + 1) {
            queue.add(new Node(popped.x, popped.y - 1, popped.distance + 1));
        }
        if ((popped.y + 1 >= 0 && popped.y + 1 < matrix.length) && matrix[popped.x][popped.y + 1] != -1 && matrix[popped.x][popped.y + 1] > popped.distance + 1) {
            queue.add(new Node(popped.x, popped.y + 1, popped.distance + 1));
        }
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
