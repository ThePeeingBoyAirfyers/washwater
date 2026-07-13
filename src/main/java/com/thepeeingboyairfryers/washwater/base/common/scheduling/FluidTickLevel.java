package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.base.common.flow.FluidFlow;
import com.thepeeingboyairfryers.washwater.base.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.InSectionTickTracker;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.PassthroughTickTracker;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.TickSectionStrategy;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FluidTickLevel implements FluidTickingContext {
    private final ThreadLocal<FluidFlow> fluidFlow = new ThreadLocal<>();
    private final Set<LongSet> nextTickToBeTicked = ConcurrentHashMap.newKeySet();
    private final ServerLevel level;
    private final Logger logger;
    private int prevProgress = 0;

    public FluidTickLevel(ServerLevel iLevel) {
        this.level = iLevel;
        logger = LoggerFactory.getLogger("FluidTick+" + iLevel.dimension().location());
    }

    public FluidTickStrategy getStrategy() {
        return FluidTickStrategy.REGISTRY.get(level);
    }

    public void tick() {
        int workAmount = FluidTickForeman.REGISTRY.get(level).calculateWork(level, prevProgress, getStrategy());
        try (PerTickTimer.Context context = WWStats.FLUID_TICKING.push()) {
            prevProgress = getStrategy().tick(context, level, this, prevProgress, workAmount);
        } catch (Exception e) {
            WashWater.LOGGER.error("Error while ticking level parallel", e);
        }
    }

    public void applyNextTicks() {
        for (LongSet toBeTicked : nextTickToBeTicked) {
            for (long p : toBeTicked) {
                toBeTicked(BlockPos.getX(p), BlockPos.getY(p), BlockPos.getZ(p));
            }
            toBeTicked.clear();
        }

        nextTickToBeTicked.clear();
    }

    @Override
    public void tickFluid(FluidRegion region, int x, int y, int z, MultiFluidValue value, int random) {
        if (value.isEmpty()) {
            WWStats.FLUID_MISSES.mark();
            return;
        }

        if (fluidFlow.get() == null) {
            fluidFlow.set(new FluidFlow());
        }

        try {
            fluidFlow.get().tick(region, new BlockPos(x, y, z));
        } catch (Exception e) {
            logger.error("Error ticking fluid at {} {} {} with {}", x, y, z, value, e);
        }
    }

    @Override
    public TickTracker makeTickTracker(TickTracker prevTickTracker, int sX, int sY, int sZ, LocalPosSet nextTickInSection) {
        if (prevTickTracker == null)
            //prevTickTracker = new SimpleTickTracker(sX, sY, sZ, this);
            prevTickTracker = new PassthroughTickTracker(sX, sY, sZ, (TickSectionStrategy) getStrategy());

        if (prevTickTracker instanceof InSectionTickTracker i) {
            i.setSectionTickList(nextTickInSection);
        }

        return prevTickTracker;
    }

    @Override
    public void updateBlock(int x, int y, int z, BlockState oldState, BlockState newState) {
        level.sendBlockUpdated(new BlockPos(x, y, z), oldState, newState, 3);
    }

    public void toBeTicked(int x, int y, int z) {
        assert MainThreads.isServerThread();
        getStrategy().toBeTicked(level, x, y, z);
    }

    public void addTickSet(LongSet toBeTicked) {
        nextTickToBeTicked.add(toBeTicked);
    }



    public ServerLevel getLevel() {
        return level;
    }
}
