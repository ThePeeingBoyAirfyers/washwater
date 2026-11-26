package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.LocalPosSet;

import java.util.BitSet;

public class LocalBitPosSet implements LocalPosSet {
    private final BitSet bits = new BitSet(16 * 16 * 16);

    @Override
    public void forEach(LocalPosConsumer consumer) {
        for (int y = 0; y < 16; y++) {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    if (bits.get(y << 8 | x << 4 | z))
                        consumer.accept(x, y, z);
                }
            }
        }
    }

    @Override
    public void add(int x, int y, int z) {
        bits.set((y & 15) << 8 | (x & 15) << 4 | (z & 15));
    }

    @Override
    public void remove(int x, int y, int z) {
        bits.clear((y & 15) << 8 | (x & 15) << 4 | (z & 15));
    }

    @Override
    public boolean isEmpty() {
        return bits.isEmpty();
    }

    @Override
    public void clear() {
        bits.clear();
    }
}
