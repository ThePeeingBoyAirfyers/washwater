package com.thepeeingboyairfryers.washwater.util.registry;

import com.mojang.serialization.Codec;
import com.thepeeingboyairfryers.washwater.util.registry.impl.LevelConfigurationRegistryImpl;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Supplier;

public interface LevelConfigurationRegistry<T extends LevelConfigurationInterface<T>> {
    Codec<T> codec();
    Registry<LevelConfigurationInterface.Entry<T>> registry();
    DeferredRegister<LevelConfigurationInterface.Entry<T>> deferredRegister(String modId);
    ResourceArgument<LevelConfigurationInterface.Entry<T>> argumentType(CommandBuildContext context);
    ResourceKey<Registry<LevelConfigurationInterface.Entry<T>>> registryKey();

    T get(ServerLevel level);

    static <T extends LevelConfigurationInterface<T>> LevelConfigurationRegistry<T> create(ResourceLocation name) {
        return new LevelConfigurationRegistryImpl<>(name, null);
    }

    static <T extends LevelConfigurationInterface<T>> LevelConfigurationRegistry<T> create(ResourceLocation name, Supplier<T> defaultValue) {
        return new LevelConfigurationRegistryImpl<>(name, defaultValue);
    }

    static LevelConfigurationRegistry<?> get(ResourceLocation location) {
        return LevelConfigurationRegistryImpl.get(location);
    }

    static Collection<LevelConfigurationRegistry<?>> allConfigurations() {
        return (Collection) LevelConfigurationRegistryImpl.allConfigurations();
    }

    @Nullable T defaultValue();

    void set(ServerLevel level, T i);
}
