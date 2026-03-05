package com.thepeeingboyairfryers.washwater.common.scheduling.section;

public interface TickTracker {
    void toBeTicked(int xW, int yW, int zW);

    void toBeUnticked(int xW, int yW, int zW);

    void apply();
}
