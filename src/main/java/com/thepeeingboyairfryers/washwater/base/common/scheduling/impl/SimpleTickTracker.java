package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickLevel;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;

public class SimpleTickTracker extends InSectionTickTracker {
    private final LongSet outSectionTicks = new LongRBTreeSet();
    private final FluidTickLevel level;

    public SimpleTickTracker(int iX, int iY, int iZ, TickSectionStrategy strategy, FluidTickLevel iLevel) {
        super(iX, iY, iZ, strategy);
        this.level = iLevel;
    }

    @Override
    protected void handleOutsideAddition(int xS, int yS, int zS, int xW, int yW, int zW) {
        outSectionTicks.add(BlockPos.asLong(xW, yW, zW));
    }

    @Override
    protected void handleOutsideRemoval(int xS, int yS, int zS, int xW, int yW, int zW) {
        outSectionTicks.remove(BlockPos.asLong(xW, yW, zW));
    }

    @Override
    public void apply() {
        super.apply();
        level.addTickSet(outSectionTicks);
    }
}
