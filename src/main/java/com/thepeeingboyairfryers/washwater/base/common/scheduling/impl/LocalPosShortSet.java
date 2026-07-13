package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.LocalPosSet;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import it.unimi.dsi.fastutil.shorts.ShortSet;

public class LocalPosShortSet implements LocalPosSet {
    private final ShortSet positions;

    public LocalPosShortSet(ShortSet iPositions) {
        this.positions = iPositions;
    }

    @Override
    public void forEach(LocalPosConsumer consumer) {
        var iter = positions.iterator();
        while (iter.hasNext()) {
            short s = iter.nextShort();
            consumer.accept(
                    FluidSection.short2localX(s),
                    FluidSection.short2localY(s),
                    FluidSection.short2localZ(s)
            );
        }
    }

    @Override
    public void add(int x, int y, int z) {
        positions.add(FluidSection.localPos2Short(x, y, z));
    }

    @Override
    public void remove(int x, int y, int z) {
        positions.remove(FluidSection.localPos2Short(x, y, z));
    }

    @Override
    public boolean isEmpty() {
        return positions.isEmpty();
    }

    @Override
    public void clear() {
        positions.clear();
    }
}
