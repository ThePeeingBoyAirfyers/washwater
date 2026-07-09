package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickLevel;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.TickTracker;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import it.unimi.dsi.fastutil.shorts.ShortSet;

public abstract class InSectionTickTracker implements TickTracker {
    private final int x;
    private final int y;
    private final int z;
    private final FluidTickLevel level;
    private ShortSet inSectionTicks;

    public InSectionTickTracker(int iX, int iY, int iZ, FluidTickLevel iLevel) {
        this.x = iX;
        this.y = iY;
        this.z = iZ;
        this.level = iLevel;
    }


    public void setSectionTickList(ShortSet set) {
        inSectionTicks = set;
    }

    @Override
    public void toBeTicked(int xW, int yW, int zW) {
        int xS = FluidTickSection.getSectionCoord(xW);
        int yS = FluidTickSection.getSectionCoord(yW);
        int zS = FluidTickSection.getSectionCoord(zW);
        if (xS == x && yS == y && zS == z) {
            inSectionTicks.add(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
        } else handleOutsideAddition(xS, yS, zS, xW, yW, zW);
    }

    protected abstract void handleOutsideAddition(int xS, int yS, int zS, int xW, int yW, int zW);

    @Override
    public void toBeUnticked(int xW, int yW, int zW) {
        int xS = FluidTickSection.getSectionCoord(xW);
        int yS = FluidTickSection.getSectionCoord(yW);
        int zS = FluidTickSection.getSectionCoord(zW);
        if (xS == x && yS == y && zS == z) {
            inSectionTicks.remove(FluidSection.localPos2Short(xW - 8, yW - 8, zW - 8));
        } else handleOutsideRemoval(xS, yS, zS, xW, yW, zW);
    }

    protected abstract void handleOutsideRemoval(int xS, int yS, int zS, int xW, int yW, int zW);

    protected FluidTickLevel getLevel() {
        return level;
    }

    protected int getX() {
        return x;
    }
    protected int getY() {
        return y;
    }
    protected int getZ() {
        return z;
    }

    @Override
    public void apply() {
        if (!inSectionTicks.isEmpty())
            level.markSectionDirty(level.getTickSection(x, y, z));
    }
}
