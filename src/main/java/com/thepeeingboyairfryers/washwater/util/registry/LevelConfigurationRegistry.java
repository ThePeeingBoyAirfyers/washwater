package com.thepeeingboyairfryers.washwater.util.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.util.registry.impl.LevelConfigurationRegistryImpl;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
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

    T get(Level level);

    static <T extends LevelConfigurationInterface<T>> LevelConfigurationRegistry<T> create(ResourceLocation name, LevelConfigurationSide side) {
        return new LevelConfigurationRegistryImpl<>(name, side, null);
    }

    static <T extends LevelConfigurationInterface<T>> LevelConfigurationRegistry<T> create(ResourceLocation name, LevelConfigurationSide side,Supplier<T> defaultValue) {
        return new LevelConfigurationRegistryImpl<>(name, side, defaultValue);
    }

    static LevelConfigurationRegistry<?> get(ResourceLocation location) {
        return LevelConfigurationRegistryImpl.get(location);
    }

    static Collection<LevelConfigurationRegistry<?>> allConfigurations() {
        return (Collection) LevelConfigurationRegistryImpl.allConfigurations();
    }

    @Nullable T defaultValue();

    void set(Level level, T i);

    LevelConfigurationSide side();

    <I extends T> LevelConfigurationInterface.Entry<T> createEntry(Class<I> clazz, MapCodec<I> codec, ConfigurationCommand<I> command);
}
