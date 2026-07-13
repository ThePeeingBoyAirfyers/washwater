package com.thepeeingboyairfryers.washwater.gameplay.common;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.serialization.Encoder;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WWCommand {
    private WWCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("ww")
                .then(stats())
                .then(configure(event.getBuildContext())));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> stats() {
        return literal("stats").executes((ctx -> {
            ctx.getSource().sendSuccess(WWStats::printReport, true);
            return 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> configure(CommandBuildContext buildContext) {
        var result =  literal("configure");

        for (var c : LevelConfigurationRegistry.allConfigurations()) {
            if (!c.registryKey().location().getNamespace().equals(WashWater.MOD_ID)) continue;
            result = result.then(literal(c.registryKey().location().getPath())
                    .then(argument("impl", c.argumentType(buildContext))
                            .then(argument("data", NbtTagArgument.nbtTag())
                                    .executes(ctx -> {
                                        LevelConfigurationInterface<?> i = ResourceArgument.getResource(ctx, "impl", c.registryKey()).value()
                                                .command().onCommand(ctx, NbtTagArgument.getNbtTag(ctx, "data"));

                                        ((LevelConfigurationRegistry) c).set(ctx.getSource().getLevel(), i);
                                        return 1;
                                    }))
                            .executes(ctx -> {
                                LevelConfigurationInterface<?> i = ResourceArgument.getResource(ctx, "impl", c.registryKey()).value()
                                        .command().onCommand(ctx, new CompoundTag());
                                ((LevelConfigurationRegistry) c).set(ctx.getSource().getLevel(), i);
                                return 1;
                            }))
                    .executes(i -> {
                        var currentConfig = c.get(i.getSource().getLevel());
                        if (currentConfig == null) {
                            i.getSource().sendFailure(Component.literal("Currently not configured"));
                            return 0;
                        }

                        Encoder<LevelConfigurationInterface<?>> encoder = (Encoder<LevelConfigurationInterface<?>>) currentConfig.codec().encoder();
                        var tag = encoder.encode(currentConfig, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();

                        i.getSource().sendSuccess(() -> NbtUtils.toPrettyComponent(tag), true);
                        return 1;
                    }));
        }

        return result;
    }
}
