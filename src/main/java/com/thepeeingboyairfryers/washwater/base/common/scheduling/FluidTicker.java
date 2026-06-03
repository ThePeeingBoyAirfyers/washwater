package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;

public class FluidTicker {
    private static final Map<ServerLevel, FluidTickLevel> TICK_LEVELS = new HashMap<>();
    private static int currentTick = 0;

    private FluidTicker() {
        throw new IllegalStateException();
    }

    public static void register(IEventBus bus) {
        NeoForge.EVENT_BUS.addListener(FluidTicker::tick);
    }

    public static void tickIfFluid(ServerLevel level, int x, int y, int z) {
        if (FluidUtil.hasFluid(level, x, y, z)) {
            tickFluid(level, x, y, z);
        }
    }

    public static void tickFluid(ServerLevel level, int x, int y, int z) {
        assert MainThreads.isMainThread();
        TICK_LEVELS.computeIfAbsent(level, FluidTickLevel::new).toBeTicked(x, y, z);
    }


    public static void tickFluid(ServerLevel level, BlockPos pos) {
        assert MainThreads.isMainThread();
        TICK_LEVELS.computeIfAbsent(level, FluidTickLevel::new).toBeTicked(pos.getX(), pos.getY(), pos.getZ());
    }


    public static boolean shouldTick(ServerLevel level) {
        return currentTick % FluidManager.lowestTick(level) == 0;
    }

    public static void tick(LevelTickEvent.Post e) {
        if (e.getLevel().isClientSide) return;
        var level = (ServerLevel) e.getLevel();
        currentTick++;

        var tLevel = TICK_LEVELS.computeIfAbsent(level, FluidTickLevel::new);

        if (shouldTick(level)) {
            tLevel.applyNextTicks();
            if (Config.PARALLEL.getAsBoolean())
                tLevel.tickLevelParallel(0, 4);
            else tLevel.tickLevelSequential(4, 4);
        } else {
            if (Config.PARALLEL.getAsBoolean())
                tLevel.tickLevelParallel(4, 4);
            else tLevel.tickLevelSequential(4, 4);
        }

        long delta = tLevel.freezeNanos();
        //WashWater.LOGGER.debug("{} freeze takes {}ms{}", level.getDescription().getString(), delta / 1000000, delta % 1000000);
    }
    public static int getCurrentTick() {
        return currentTick;
    }
}
