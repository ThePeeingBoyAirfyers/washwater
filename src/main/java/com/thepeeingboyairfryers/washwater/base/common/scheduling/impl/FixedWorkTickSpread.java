package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSpread;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickStrategy;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.minecraft.nbt.IntTag;
import net.minecraft.server.level.ServerLevel;

public class FixedWorkTickSpread implements FluidTickSpread {
    public static final MapCodec<FixedWorkTickSpread> CODEC = RecordCodecBuilder.mapCodec((b) -> b.group(
            Codec.INT.fieldOf("fixedWork").forGetter(FixedWorkTickSpread::getFixedWork)
    ).apply(b, FixedWorkTickSpread::new));

    public static final ConfigurationCommand<FixedWorkTickSpread> COMMAND = (ctx, data) -> {
        if (data.getType() != IntTag.TYPE) CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherParseException();
        return new FixedWorkTickSpread(((IntTag) data).getAsInt());
    };

    private final int fixedWork;

    public FixedWorkTickSpread(int iFixedWork) {
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
    public MapCodec<? extends FluidTickSpread> codec() {
        return CODEC;
    }

    @Override
    public ConfigurationCommand<? extends FluidTickSpread> command() {
        return COMMAND;
    }
}
