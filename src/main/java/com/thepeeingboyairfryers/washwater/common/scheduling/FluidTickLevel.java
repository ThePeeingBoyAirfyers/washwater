package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.common.flow.FluidFlow;
import com.thepeeingboyairfryers.washwater.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
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
    private final ServerLevel level;
    private final Long2ObjectMap<FluidTickSection> tickSections = new Long2ObjectAVLTreeMap<>();
    private final Set<FluidTickSection>[] dirtySections;
    private final Set<LongSet> nextTickToBeTicked = ConcurrentHashMap.newKeySet();
    private final Executor executor = Executors.newFixedThreadPool(8);

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
        for (int p = 0; p < length; p++) {
            var toBeTicked = dirtySections[p + offset];
            if (toBeTicked.isEmpty()) continue;

            dirtySections[p + offset] = new HashSet<>();
            CompletableFuture<Void>[] futures = new CompletableFuture[toBeTicked.size()];

            var iter = toBeTicked.iterator();
            for (int i = 0; i < futures.length; i++) {
                var section = iter.next();
                if (section.getAge() + REFRESH_RATE < FluidTicker.getCurrentTick())
                    setupTicker(section);

                futures[i] = CompletableFuture.runAsync(() -> section.tick(this), executor);
            }

            CompletableFuture.allOf(futures).join();
        }
    }

    public void applyNextTicks() {
        for (LongSet toBeTicked : nextTickToBeTicked) {
            for (long p : toBeTicked) {
                toBeTicked(BlockPos.getX(p), BlockPos.getY(p), BlockPos.getZ(p));
            }
        }

        nextTickToBeTicked.clear();
    }

    private FluidTickSection setupTicker(FluidTickSection section) {
        section.fetchSections(level);
        section.setAge(FluidTicker.getCurrentTick());
        return section;
    }

    @Override
    public void tickFluid(FluidRegion region, int x, int y, int z, MultiFluidValue value, int random) {
        FluidFlow.tick(region, new BlockPos(x, y, z));
    }

    public void toBeTicked(int x, int y, int z) {
        int xS = (x - 8) >> 4;
        int yS = (y - 8) >> 4;
        int zS = (z - 8) >> 4;

        FluidTickSection tSection = tickSections.computeIfAbsent(SectionPos.asLong(xS, yS, zS), l -> setupTicker(new FluidTickSection(xS, yS, zS)));
        tSection.addLiveTick(x, y, z);

        dirtySections[FluidTickSection.getPhase(xS, yS, zS)].add(tSection);
    }

    @Override
    public void submitTickSet(LongSet toBeTicked) {
        nextTickToBeTicked.add(toBeTicked);
    }
}
