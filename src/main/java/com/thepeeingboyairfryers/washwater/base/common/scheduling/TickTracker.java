package com.thepeeingboyairfryers.washwater.base.common.scheduling;

public interface TickTracker {
    void toBeTicked(int xW, int yW, int zW);

    void toBeUnticked(int xW, int yW, int zW);

    void apply();
}
