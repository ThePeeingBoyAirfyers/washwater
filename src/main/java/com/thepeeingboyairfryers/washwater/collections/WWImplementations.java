package com.thepeeingboyairfryers.washwater.collections;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickForeman;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.FixedWorkForeman;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.ManualForeman;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.ParallelTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.impl.SequentialSectionTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeStrategy;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.DefaultFluidSectionUpgradeStrategy;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.DumbFluidSectionUpgradeStrategy;
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
        register(FluidTickForeman.REGISTRY, FixedWorkForeman.class, "fixed_work", FixedWorkForeman.CODEC, FixedWorkForeman.COMMAND);
        register(FluidTickForeman.REGISTRY, ManualForeman.class, "manual", ManualForeman.CODEC, ManualForeman.COMMAND);

        register(FluidTickStrategy.REGISTRY, ParallelTickStrategy.class, "parallel", ParallelTickStrategy.CODEC, ParallelTickStrategy.COMMAND);
        register(FluidTickStrategy.REGISTRY, SequentialSectionTickStrategy.class, "section_sequential", SequentialSectionTickStrategy.CODEC, SequentialSectionTickStrategy.COMMAND);

        register(FluidSectionUpgradeStrategy.REGISTRY, DefaultFluidSectionUpgradeStrategy.class, "default", DefaultFluidSectionUpgradeStrategy.INSTANCE.codec(), DefaultFluidSectionUpgradeStrategy.INSTANCE.command());
        register(FluidSectionUpgradeStrategy.REGISTRY, DumbFluidSectionUpgradeStrategy.class, "dumb", DumbFluidSectionUpgradeStrategy.INSTANCE.codec(), DumbFluidSectionUpgradeStrategy.INSTANCE.command());

        bus.addListener((RegisterEvent e) ->
                entries.forEach((r, l) ->
                        e.register(r.registryKey(), helper ->
                                l.forEach(p -> helper.register(WashWater.resource(p.key()), (LevelConfigurationInterface.Entry) p.value())))));
    }

    private static <I extends LevelConfigurationInterface<I>, T extends I> void register(
            LevelConfigurationRegistry<I> r,
            Class<T> clazz,
            String name,
            MapCodec<T> codec,
            ConfigurationCommand<T> command
    ) {
        entries.computeIfAbsent(r, i -> new ArrayList<>())
                .add(Pair.of(name, r.createEntry(clazz, codec, command)));
    }
    private WWImplementations() {
        throw new IllegalStateException("Utility class");
    }
}
