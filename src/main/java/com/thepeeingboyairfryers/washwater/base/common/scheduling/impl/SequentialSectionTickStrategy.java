package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickingContext;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.minecraft.nbt.IntTag;

import java.util.Set;

public class SequentialSectionTickStrategy extends TickSectionStrategy {
    public static final MapCodec<SequentialSectionTickStrategy> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.INT.fieldOf("refreshRate").forGetter(SequentialSectionTickStrategy::getRefreshRate)
    ).apply(i, SequentialSectionTickStrategy::new));
    public static final ConfigurationCommand<SequentialSectionTickStrategy> COMMAND = (ctx, data) -> {
        if (data.getType() != IntTag.TYPE) CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException();
        return new SequentialSectionTickStrategy(((IntTag) data).getAsInt());
    };

    public SequentialSectionTickStrategy(int iRefreshRate) {
        super(iRefreshRate);
    }

    @Override
    protected void doWork(PerTickTimer.Context timerCtx, int phase, Set<FluidTickSection> toBeTicked, FluidTickingContext ctx) {
        for (var section : toBeTicked) {
            section.tick(ctx);
        }
    }

    @Override
    public MapCodec<? extends FluidTickStrategy> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends FluidTickStrategy> command() {
        return COMMAND;
    }
}
