package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.common.flow.FluidFlow;
import com.thepeeingboyairfryers.washwater.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.common.flow.SimpleFluidRegion;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.util.SwapPair;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class FluidTicker {
    private static int counter = 0;
    private static final Map<ServerLevel, FluidRegion> REGIONS = new HashMap<>();
    private static final Map<ServerLevel, SwapPair<LongSet>> WATERS = new HashMap<>();
    private static int currentTick = 0;

    private FluidTicker() {
        throw new IllegalStateException();
    }

    public static void tickWater(ServerLevel level, BlockPos pos) {
        getCurrentWaterList(level).add(pos.asLong());
    }

    public static void tickWater(ServerLevel level, int x, int y, int z) {
        getCurrentWaterList(level).add(BlockPos.asLong(x, y, z));
    }

    public static void tickIfWater(ServerLevel level, int x, int y, int z) {
        if (FluidUtil.hasFluid(level, x, y, z)) {
            getCurrentWaterList(level).add(BlockPos.asLong(x, y, z));
        }
    }

    public static boolean shouldTick(ServerLevel level) {
        return counter % FluidManager.lowestTick(level) == 0;
    }

    public static boolean shouldClearRegions(ServerLevel level) {
        return counter % (FluidManager.lowestTick(level) * 100) == 0;
    }

    public static void tick(ServerLevel level) {
        currentTick++;

        if (shouldTick(level)) {
            LongSet activeChunks = new LongOpenHashSet();
            ActiveChunks.getActiveChunks(level, activeChunks);

            var region = REGIONS.computeIfAbsent(level, level1 -> new SimpleFluidRegion(level1, l -> {
                int x = BlockPos.getX(l);
                int y = BlockPos.getY(l);
                int z = BlockPos.getZ(l);
                if (FluidUtil.hasFluid(level, x, y, z)) {
                    getCurrentWaterList(level).add(BlockPos.asLong(x, y, z));
                }
            }));
            var pair = WATERS.get(level);
            if (pair == null) return;

            pair.swap();
            for (long pos : pair.getOther()) {
                var bPos = BlockPos.of(pos);
                var chunkPos = ChunkPos.asLong(bPos.getX() >> 4, bPos.getZ() >> 4);

                //if (activeChunks.contains(chunkPos)) {
                    FluidFlow.tick(region, bPos);
                //}
            }

            pair.getOther().clear();

        }

        if (shouldClearRegions(level)) {
            REGIONS.clear();
            counter = 0;
        }

        counter++;
    }

    private static LongSet getCurrentWaterList(ServerLevel level) {
        return WATERS.computeIfAbsent(level, k -> new SwapPair<>(new LongOpenHashSet(), new LongOpenHashSet())).getCurrent();
    }

    public static int getCurrentTick() {
        return currentTick;
    }
}
