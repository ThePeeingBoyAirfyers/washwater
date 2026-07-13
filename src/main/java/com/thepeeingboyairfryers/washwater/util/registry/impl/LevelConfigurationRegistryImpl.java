package com.thepeeingboyairfryers.washwater.util.registry.impl;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LevelConfigurationRegistryImpl<T extends LevelConfigurationInterface<T>> implements LevelConfigurationRegistry<T> {
    private static final Map<ResourceKey<Registry<LevelConfigurationInterface.Entry<?>>>, LevelConfigurationRegistryImpl<?>> CONFIGURATIONS = new HashMap<>();
    private final ResourceKey<Registry<LevelConfigurationInterface.Entry<T>>> registryKey;
    private final Registry<LevelConfigurationInterface.Entry<T>> registry;
    private final Codec<T> codec;
    private ResourceArgument<LevelConfigurationInterface.Entry<T>> argumentType = null;
    private final Supplier<T> defaultValue;

    public LevelConfigurationRegistryImpl(ResourceLocation iName, Supplier<T> iDefaultValue) {
        registryKey = ResourceKey.createRegistryKey(iName);
        registry = new RegistryBuilder<>(registryKey).create();

        codec = registry.byNameCodec().dispatch(i -> new LevelConfigurationInterface.Entry<T>() {
            @Override
            public ConfigurationCommand<? extends T> command() {
                return i.command();
            }

            @Override
            public MapCodec<? extends T> codec() {
                return i.codec();
            }
        }, LevelConfigurationInterface.Entry::codec);

        defaultValue = iDefaultValue;
        CONFIGURATIONS.put((ResourceKey) registryKey, this);
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public Registry<LevelConfigurationInterface.Entry<T>> registry() {
        return registry;
    }

    @Override
    public DeferredRegister<LevelConfigurationInterface.Entry<T>> deferredRegister(String modId) {
        return DeferredRegister.create(registryKey, modId);
    }

    @Override
    public ResourceArgument<LevelConfigurationInterface.Entry<T>> argumentType(CommandBuildContext context) {
        if (argumentType == null) {
            argumentType = ResourceArgument.resource(context, registryKey);
        }

        return argumentType;
    }

    public static Collection<LevelConfigurationRegistryImpl<?>> allConfigurations() {
        return CONFIGURATIONS.values();
    }

    public static LevelConfigurationRegistryImpl get(ResourceLocation location) {
        return CONFIGURATIONS.get(ResourceKey.createRegistryKey(location));
    }

    @Override
    public ResourceKey<Registry<LevelConfigurationInterface.Entry<T>>> registryKey() {
        return registryKey;
    }

    @Override
    public T get(ServerLevel level) {
        return level.getData(WWAttachments.CONFIGURATIONS).get(this);
    }

    @Override
    public int hashCode() {
        return registryKey.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public @Nullable T defaultValue() {
        return defaultValue.get();
    }

    @Override
    public void set(ServerLevel level, T i) {
        level.getData(WWAttachments.CONFIGURATIONS).set(this, i);
    }
}
