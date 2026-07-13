package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickForeman;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;

public class FixedWorkForeman implements FluidTickForeman {
    public static final MapCodec<FixedWorkForeman> CODEC = RecordCodecBuilder.mapCodec((b) -> b.group(
            Codec.INT.fieldOf("fixedWork").forGetter(FixedWorkForeman::getFixedWork)
    ).apply(b, FixedWorkForeman::new));

    public static final ConfigurationCommand<FixedWorkForeman> COMMAND = (ctx, data) -> {
        if (data.getType() != IntTag.TYPE) CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException();
        return new FixedWorkForeman(((IntTag) data).getAsInt());
    };

    private final int fixedWork;

    public FixedWorkForeman(int iFixedWork) {
        fixedWork = iFixedWork;
    }

    @Override
    public int calculateWork(ServerLevel level, int currentProgress, FluidTickStrategy strategy) {
        return 4;
    }

    public int getFixedWork() {
        return fixedWork;
    }

    @Override
    public MapCodec<? extends FluidTickForeman> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends FluidTickForeman> command() {
        return COMMAND;
    }
}
