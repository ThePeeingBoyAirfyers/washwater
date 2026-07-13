package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.base.common.flow.FluidFlow;
import com.thepeeingboyairfryers.washwater.base.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.InSectionTickTracker;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.PassthroughTickTracker;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FluidTickLevel implements FluidTickingContext {
    private static final int REFRESH_RATE = 1000;
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(decideThreadCount());
    private final ThreadLocal<FluidFlow> fluidFlow = new ThreadLocal<>();
    private final ServerLevel level;
    private final Long2ObjectMap<FluidTickSection> tickSections = new Long2ObjectAVLTreeMap<>();
    private final Set<FluidTickSection>[] dirtySections;
    private final Set<LongSet> nextTickToBeTicked = ConcurrentHashMap.newKeySet();
    private Logger logger;

    public FluidTickLevel(ServerLevel iLevel) {
        this.level = iLevel;
        dirtySections = new HashSet[]{
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
        };

        logger = LoggerFactory.getLogger("FluidTick+" + iLevel.dimension().location());
    }

    private static int decideThreadCount() {
        int config = Config.THREAD_COUNT.getAsInt();
        if (config != 0) return config;

        return Runtime.getRuntime().availableProcessors() - 2;
    }

    public void tickLevelSequential(int offset, int length) {
        for (int p = 0; p < length; p++) {
            var old = dirtySections[p + offset];
            dirtySections[p + offset] = new HashSet<>();
            for (var section : old) {
                if (section.needsRefetch() || section.getAge() + REFRESH_RATE < FluidTicker.getCurrentTick())
                    setupTicker(section);

                section.tick(this);
            }
        }
    }

    public void tickLevelParallel(int offset, int length) {
        int amountOfDirtySections = 0;

        try (PerTickTimer.Context context = WWStats.FLUID_TICKING.push()) {
            for (int p = 0; p < length; p++) {

                var toBeTicked = dirtySections[p + offset];
                if (toBeTicked.isEmpty()) continue;

                amountOfDirtySections += toBeTicked.size();
                dirtySections[p + offset] = new HashSet<>();

                var iter = toBeTicked.iterator();
                while (iter.hasNext()) {
                    var section = iter.next();
                    createNeighbors(section.getX(), section.getY(), section.getZ());

                    if (section.needsRefetch() || section.getAge() + REFRESH_RATE < FluidTicker.getCurrentTick())
                        setupTicker(section);

                    if (!section.canTick())
                        iter.remove();
                }

                CompletableFuture<Void>[] futures = new CompletableFuture[toBeTicked.size()];
                iter = toBeTicked.iterator();
                for (int i = 0; i < futures.length; i++) {
                    var section = iter.next();
                    futures[i] = CompletableFuture.runAsync(() -> section.tick(this), EXECUTOR);
                }

                try (PerTickTimer.Context c = context.push("Pool")) {
                    CompletableFuture.allOf(futures).join();
                }
            }
        } catch (Exception e) {
            WashWater.LOGGER.error("Error while ticking level parallel", e);
        }

        WWStats.TICKED_SECTIONS.pushMeasurement(amountOfDirtySections);
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

    private void createNeighbors(int xS, int yS, int zS) {
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    int xx = x + xS;
                    int yy = y + yS;
                    int zz = z + zS;

                    if (yy < level.getMinSection() - 1) continue;
                    var section = tickSections.computeIfAbsent(
                            SectionPos.asLong(xx, yy, zz),
                            l -> new FluidTickSection(xx, yy, zz));

                    section.setAge(FluidTicker.getCurrentTick() - REFRESH_RATE - 10);
                }
            }
        }
    }

    private FluidTickSection setupTicker(FluidTickSection section) {
        section.fetchSections(level);
        section.setAge(FluidTicker.getCurrentTick() + (level.random.nextInt() % 200)); // Spread out
        return section;
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
    public TickTracker makeTickTracker(TickTracker prevTickTracker, int sX, int sY, int sZ, ShortSet nextTickInSection) {
        if (prevTickTracker == null)
            //prevTickTracker = new SimpleTickTracker(sX, sY, sZ, this);
            prevTickTracker = new PassthroughTickTracker(sX, sY, sZ, this);

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

        int xS = FluidTickSection.getSectionCoord(x);
        int yS = FluidTickSection.getSectionCoord(y);
        int zS = FluidTickSection.getSectionCoord(z);

        FluidTickSection tSection = tickSections.computeIfAbsent(SectionPos.asLong(xS, yS, zS), l -> setupTicker(new FluidTickSection(xS, yS, zS)));
        tSection.addLiveTick(x, y, z);
        markSectionDirty(tSection);
    }

    public void markSectionDirty(FluidTickSection section) {
        var set = dirtySections[FluidTickSection.getPhase(section.getX(), section.getY(), section.getZ())];
        synchronized (set) {
            set.add(section);
        }
    }

    public void addTickSet(LongSet toBeTicked) {
        nextTickToBeTicked.add(toBeTicked);
    }

    public FluidTickSection getTickSection(int xS, int yS, int zS) {
        return tickSections.get(SectionPos.asLong(xS, yS, zS));
    }

    public ServerLevel getLevel() {
        return level;
    }
}
