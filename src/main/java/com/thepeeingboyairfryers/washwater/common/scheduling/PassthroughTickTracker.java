package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.common.scheduling.section.FluidTickSection;

import java.util.HashSet;
import java.util.Set;

public class PassthroughTickTracker extends InSectionTickTracker {
    private final int myPhase;
    private final FluidTickLevel level;
    private final Set<FluidTickSection> dirtySections = new HashSet<>();

    public PassthroughTickTracker(int iX, int iY, int iZ, FluidTickLevel iLevel) {
        super(iX, iY, iZ, iLevel);
        this.myPhase = FluidTickSection.getPhase(iX, iY, iZ);
        this.level = iLevel;
    }

    @Override
    protected void handleOutsideAddition(int xS, int yS, int zS, int xW, int yW, int zW) {
        int phase = FluidTickSection.getPhase(xS, yS, zS);
        if (phase == myPhase) throw new IllegalStateException();

        var section = level.getTickSection(xS, yS, zS);
        dirtySections.add(section);
        if (phase < myPhase) {
            section.addLiveTick(xW, yW, zW);
        } else {
            section.addLaterTick(xW, yW, zW);
        }
    }

    @Override
    protected void handleOutsideRemoval(int xS, int yS, int zS, int xW, int yW, int zW) {
        int phase = FluidTickSection.getPhase(xS, yS, zS);
        if (phase == myPhase) throw new IllegalStateException();

        var section = level.getTickSection(xS, yS, zS);
        if (phase < myPhase) {
            section.removeLiveTick(xW, yW, zW);
        } else {
            section.removeLaterTick(xW, yW, zW);
        }
    }

    @Override
    public void apply() {
        for (var section : dirtySections) {
            level.markSectionDirty(section);
        }
    }
}
