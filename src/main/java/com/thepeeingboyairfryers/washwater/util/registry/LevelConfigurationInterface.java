package com.thepeeingboyairfryers.washwater.util.registry;

import com.mojang.serialization.MapCodec;

public interface LevelConfigurationInterface<T extends LevelConfigurationInterface<T>> {
    MapCodec<? extends T> codec();
    ConfigurationCommand<? extends T> command();

    interface Entry<T extends LevelConfigurationInterface<T>> {
        ConfigurationCommand<? extends T> command();
        MapCodec<? extends T> codec();
    }
}
