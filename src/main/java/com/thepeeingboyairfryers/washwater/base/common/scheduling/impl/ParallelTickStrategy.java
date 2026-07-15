package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickingContext;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.minecraft.nbt.IntTag;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ParallelTickStrategy extends TickSectionStrategy {
    public static final MapCodec<ParallelTickStrategy> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("refreshRate").forGetter(ParallelTickStrategy::getRefreshRate),
            Codec.INT.fieldOf("threadCount").forGetter(ParallelTickStrategy::getThreadCount)
    ).apply(i, ParallelTickStrategy::new));
    public static final ConfigurationCommand<ParallelTickStrategy> COMMAND = (ctx, data) -> {
        if (data.getType() != IntTag.TYPE) CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException();
        return new ParallelTickStrategy(((IntTag) data).getAsInt());
    };

    private final Executor executor;

    public ParallelTickStrategy(int iRefreshRate) {
        this(iRefreshRate, decideThreadCount());
    }

    public ParallelTickStrategy(int iRefreshRate, int threadCount) {
        super(iRefreshRate);
        executor = Executors.newFixedThreadPool(threadCount);
    }

    @Override
    public MapCodec<? extends FluidTickStrategy> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends FluidTickStrategy> command() {
        return COMMAND;
    }

    @Override
    protected void doWork(PerTickTimer.Context timerCtx, int phase, Set<FluidTickSection> toBeTicked, FluidTickingContext ctx) {
        CompletableFuture<Void>[] futures = new CompletableFuture[toBeTicked.size()];
        var iter = toBeTicked.iterator();
        for (int i = 0; i < futures.length; i++) {
            var section = iter.next();
            futures[i] = CompletableFuture.runAsync(() -> section.tick(ctx), executor);
        }

        try (PerTickTimer.Context c = timerCtx.push("Waiting")) {
            CompletableFuture.allOf(futures).join();
        }
    }

    public int getThreadCount() {
        return ((ThreadPoolExecutor) executor).getPoolSize();
    }

    private static int decideThreadCount() {
        int config = Config.THREAD_COUNT.getAsInt();
        if (config != 0) return config;

        return Runtime.getRuntime().availableProcessors() - 2;
    }
}
