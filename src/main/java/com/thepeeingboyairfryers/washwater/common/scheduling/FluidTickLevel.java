package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.common.Config;
import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.flow.FluidFlow;
import com.thepeeingboyairfryers.washwater.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.scheduling.section.FluidTickSection;
import com.thepeeingboyairfryers.washwater.common.scheduling.section.FluidTickingContext;
import com.thepeeingboyairfryers.washwater.common.scheduling.section.TickTracker;
import com.thepeeingboyairfryers.washwater.common.util.parallel.MainThreads;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FluidTickLevel implements FluidTickingContext {
    private static final int REFRESH_RATE = 100;
    private static final Executor EXECUTOR = Executors.newFixedThreadPool(Config.THREAD_COUNT.getAsInt());
    private final ServerLevel level;
    private final Long2ObjectMap<FluidTickSection> tickSections = new Long2ObjectAVLTreeMap<>();
    private final Set<FluidTickSection>[] dirtySections;
    private final Set<LongSet> nextTickToBeTicked = ConcurrentHashMap.newKeySet();
    private int misTicks = 0;
    private long frozenTime = 0;

    public FluidTickLevel(ServerLevel iLevel) {
        this.level = iLevel;
        dirtySections = new HashSet[] {
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
        };
    }

    public void tickLevelSequential(int offset, int length) {
        for (int p = 0; p < length; p++) {
            var old = dirtySections[p + offset];
            dirtySections[p + offset] = new HashSet<>();
            for (var section : old) {
                if (section.getAge() + REFRESH_RATE < FluidTicker.getCurrentTick())
                    setupTicker(section);

                section.tick(this);
            }
        }
    }

    public void tickLevelParallel(int offset, int length) {
        frozenTime = 0;
        for (int p = 0; p < length; p++) {
            var toBeTicked = dirtySections[p + offset];
            if (toBeTicked.isEmpty()) continue;

            dirtySections[p + offset] = new HashSet<>();
            CompletableFuture<Void>[] futures = new CompletableFuture[toBeTicked.size()];

            for (var section : toBeTicked) {
                createNeighbors(section.getX(), section.getY(), section.getZ());
            }

            var iter = toBeTicked.iterator();
            for (int i = 0; i < futures.length; i++) {
                var section = iter.next();
                if (section.getAge() + REFRESH_RATE < FluidTicker.getCurrentTick())
                    setupTicker(section);

                futures[i] = CompletableFuture.runAsync(() -> section.tick(this), EXECUTOR);
            }

            long start = System.nanoTime();
            CompletableFuture.allOf(futures).join();
            frozenTime += (System.nanoTime() - start);
        }
    }

    public void applyNextTicks() {
        if (misTicks > 0) {
            WashWater.LOGGER.warn("Empty fluids were ticked {} times", misTicks);
            misTicks = 0;
        }

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
            misTicks++;
            return;
        }

        FluidFlow.tick(region, new BlockPos(x, y, z));
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

    public long freezeNanos() {
        return frozenTime;
    }
}
