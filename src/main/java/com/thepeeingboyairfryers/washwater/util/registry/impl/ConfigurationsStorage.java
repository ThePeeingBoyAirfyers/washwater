package com.thepeeingboyairfryers.washwater.util.registry.impl;

import com.mojang.serialization.Codec;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationsStorage {
    public static final Codec<ConfigurationsStorage> CODEC = Codec.dispatchedMap(ResourceLocation.CODEC, ConfigurationsStorage::getInterfaceCodec)
            .xmap(ConfigurationsStorage::new, ConfigurationsStorage::toMap);

    private final Map<LevelConfigurationRegistry<?>, LevelConfigurationInterface<?>> configurations = new HashMap<>();

    public ConfigurationsStorage() {

    }

    private ConfigurationsStorage(Map entries) {
        entries.forEach((Object l, Object o) ->
                configurations.put(LevelConfigurationRegistry.get((ResourceLocation) l), (LevelConfigurationInterface<?>) o));
    }

    private Map toMap() {
        Map<ResourceLocation, Object> process = new HashMap<>();
        configurations.forEach((key, value) -> {
            process.put(key.registryKey().location(), value);
        });

        return process;
    }

    private static Codec<?> getInterfaceCodec(ResourceLocation resourceLocation) {
        return LevelConfigurationRegistry.get(resourceLocation).codec();
    }

    public <T extends LevelConfigurationInterface<T>> T get(LevelConfigurationRegistry<T> registry) {
        T result = (T) configurations.get(registry);
        if (result == null) {
            result = registry.defaultValue();
            if (result != null) configurations.put(registry, result);
        }

        return result;
    }

    public <T extends LevelConfigurationInterface<T>> void set(LevelConfigurationRegistry<T> registry, T i) {
        configurations.put(registry, i);
    }
}
