package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickingContext;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;

public abstract class TickSectionStrategy implements FluidTickStrategy {
    private final int refreshRate;
    private final Long2ObjectMap<FluidTickSection> tickSections = new Long2ObjectAVLTreeMap<>();
    private final Set<FluidTickSection>[] dirtySections = new HashSet[]{
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
            new HashSet<>(),
    };

    protected TickSectionStrategy(int iRefreshRate) {
        refreshRate = iRefreshRate;
    }

    protected abstract void doWork(PerTickTimer.Context timerCtx, int phase, Set<FluidTickSection> toBeTicked, FluidTickingContext ctx);

    @Override
    public int tick(PerTickTimer.Context timerCtx, ServerLevel level, FluidTickingContext ctx, int prevProgress, int workAmount) {
        int amountOfDirtySections = 0;

        for (int p = 0; p < workAmount; p++) {
            int phase = (p + prevProgress) % 8;
            var toBeTicked = dirtySections[phase];
            if (toBeTicked.isEmpty()) continue;

            amountOfDirtySections += toBeTicked.size();
            dirtySections[phase] = new HashSet<>();

            var iter = toBeTicked.iterator();
            while (iter.hasNext()) {
                var section = iter.next();
                createNeighbors(level, section.getX(), section.getY(), section.getZ());

                if (section.needsRefetch() || section.getAge() + refreshRate < FluidTicker.getCurrentTick())
                    setupTicker(level, section);

                if (!section.canTick())
                    iter.remove();
            }

            doWork(timerCtx, phase, toBeTicked, ctx);
        }

        WWStats.TICKED_SECTIONS.pushMeasurement(amountOfDirtySections, level.dimension().location() + " TickSections");

        return (prevProgress + workAmount) % 8;
    }

    @Override
    public void toBeTicked(ServerLevel level, int x, int y, int z) {
        int xS = FluidTickSection.getSectionCoord(x);
        int yS = FluidTickSection.getSectionCoord(y);
        int zS = FluidTickSection.getSectionCoord(z);

        FluidTickSection tSection = tickSections.computeIfAbsent(SectionPos.asLong(xS, yS, zS), l -> setupTicker(level, new FluidTickSection(xS, yS, zS)));
        tSection.addLiveTick(x, y, z);
        markSectionDirty(tSection);
    }

    public void markSectionDirty(FluidTickSection section) {
        var set = dirtySections[FluidTickSection.getPhase(section.getX(), section.getY(), section.getZ())];
        synchronized (set) { // TODO take a look at this
            set.add(section);
        }
    }

    public FluidTickSection getTickSection(int xS, int yS, int zS) {
        return tickSections.get(SectionPos.asLong(xS, yS, zS));
    }

    public int getRefreshRate() {
        return refreshRate;
    }

    private void createNeighbors(ServerLevel level, int xS, int yS, int zS) {
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

                    section.setAge(FluidTicker.getCurrentTick() - refreshRate - 10);
                }
            }
        }
    }

    private FluidTickSection setupTicker(ServerLevel level, FluidTickSection section) {
        section.fetchSections(level);
        section.setAge(FluidTicker.getCurrentTick() + (level.random.nextInt() % 200)); // Spread out

        return section;
    }
}
