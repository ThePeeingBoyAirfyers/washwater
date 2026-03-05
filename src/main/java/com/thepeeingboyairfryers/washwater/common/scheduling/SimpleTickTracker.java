package com.thepeeingboyairfryers.washwater.common.scheduling;

import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;

public class SimpleTickTracker extends InSectionTickTracker {
    private final LongSet outSectionTicks = new LongRBTreeSet();

    public SimpleTickTracker(int iX, int iY, int iZ, FluidTickLevel iLevel) {
        super(iX, iY, iZ, iLevel);
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
        getLevel().addTickSet(outSectionTicks);
    }
}
