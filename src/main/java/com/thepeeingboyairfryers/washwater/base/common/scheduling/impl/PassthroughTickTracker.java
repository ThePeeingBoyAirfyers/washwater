package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;

import java.util.HashSet;
import java.util.Set;

public class PassthroughTickTracker extends InSectionTickTracker {
    private final int myPhase;
    private final Set<FluidTickSection> dirtySections = new HashSet<>();

    public PassthroughTickTracker(int iX, int iY, int iZ, TickSectionStrategy iStrategy) {
        super(iX, iY, iZ, iStrategy);
        this.myPhase = FluidTickSection.getPhase(iX, iY, iZ);
    }

    @Override
    protected void handleOutsideAddition(int xS, int yS, int zS, int xW, int yW, int zW) {
        int phase = FluidTickSection.getPhase(xS, yS, zS);
        if (phase == myPhase) throw new IllegalStateException();

        var section = getStrategy().getTickSection(xS, yS, zS);
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

        var section = getStrategy().getTickSection(xS, yS, zS);
        if (phase < myPhase) {
            section.removeLiveTick(xW, yW, zW);
        } else {
            section.removeLaterTick(xW, yW, zW);
        }
    }

    @Override
    public void apply() {
        super.apply();
        for (var section : dirtySections) {
            getStrategy().markSectionDirty(section);
        }
    }
}
