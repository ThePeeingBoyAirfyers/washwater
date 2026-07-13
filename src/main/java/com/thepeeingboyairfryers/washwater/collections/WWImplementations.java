package com.thepeeingboyairfryers.washwater.collections;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSpread;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.FixedWorkTickSpread;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.ParallelTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.SequentialSectionTickStrategy;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import it.unimi.dsi.fastutil.Pair;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WWImplementations {
    private static HashMap<LevelConfigurationRegistry<?>, List<Pair<String, LevelConfigurationInterface.Entry<?>>>> entries = new HashMap<>();

    public static void register(IEventBus bus) {
        register(FluidTickSpread.REGISTRY, "fixed_work", FixedWorkTickSpread.CODEC, FixedWorkTickSpread.COMMAND);

        register(FluidTickStrategy.REGISTRY, "parallel", ParallelTickStrategy.CODEC, ParallelTickStrategy.COMMAND);
        register(FluidTickStrategy.REGISTRY, "section_sequential", SequentialSectionTickStrategy.CODEC, SequentialSectionTickStrategy.COMMAND);

        bus.addListener((RegisterEvent e) ->
                entries.forEach((r, l) ->
                        e.register(r.registryKey(), helper ->
                                l.forEach(p -> helper.register(WashWater.resource(p.key()), (LevelConfigurationInterface.Entry) p.value())))));
    }

    private static <I extends LevelConfigurationInterface<I>, T extends LevelConfigurationInterface<I>> void register(
            LevelConfigurationRegistry<I> r,
            String name,
            MapCodec<T> codec,
            ConfigurationCommand<T> command
    ) {
        entries.computeIfAbsent(r, i -> new ArrayList<>())
                .add(Pair.of(name, new LevelConfigurationInterface.Entry<I>() {
                    @Override
                    public ConfigurationCommand<? extends I> command() {
                        return (ConfigurationCommand<? extends I>) command;
                    }

                    @Override
                    public MapCodec<? extends I> codec() {
                        return (MapCodec<? extends I>) codec;
                    }
                }));
    }
    private WWImplementations() {
        throw new IllegalStateException("Utility class");
    }
}
