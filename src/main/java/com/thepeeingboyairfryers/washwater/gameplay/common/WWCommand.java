package com.thepeeingboyairfryers.washwater.gameplay.common;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.serialization.Encoder;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationSide;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class WWCommand {
    private WWCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static void registerServerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("ww")
                .then(stats())
                .then(configure(event.getBuildContext(), false)));
    }

    public static void registerClientCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("wwc")
                .then(configure(event.getBuildContext(), true)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> stats() {
        return literal("stats").executes((ctx -> {
            ctx.getSource().sendSuccess(WWStats::printReport, true);
            return 0;
        }));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> configure(CommandBuildContext buildContext, boolean isClient) {
        var result =  literal("configure");

        for (var c : LevelConfigurationRegistry.allConfigurations()) {
            if (!c.registryKey().location().getNamespace().equals(WashWater.MOD_ID)) continue;
            if (c.side() == LevelConfigurationSide.CLIENT && !isClient) continue;
            if (c.side() == LevelConfigurationSide.SERVER && isClient) continue;

            result = result.then(literal(c.registryKey().location().getPath())
                    .then(argument("impl", c.argumentType(buildContext))
                            .then(argument("data", NbtTagArgument.nbtTag())
                                    .executes(ctx -> {
                                        LevelConfigurationInterface<?> i = ResourceArgument.getResource(ctx, "impl", c.registryKey()).value()
                                                .command().onCommand(ctx, NbtTagArgument.getNbtTag(ctx, "data"));

                                        ((LevelConfigurationRegistry) c).set(ctx.getSource().getUnsidedLevel(), i);
                                        return 1;
                                    }))
                            .executes(ctx -> {
                                LevelConfigurationInterface<?> i = ResourceArgument.getResource(ctx, "impl", c.registryKey()).value()
                                        .command().onCommand(ctx, new CompoundTag());
                                ((LevelConfigurationRegistry) c).set(ctx.getSource().getUnsidedLevel(), i);
                                return 1;
                            }))
                    .executes(i -> {
                        var currentConfig = c.get(i.getSource().getUnsidedLevel());
                        if (currentConfig == null) {
                            i.getSource().sendFailure(Component.literal("Currently not configured"));
                            return 0;
                        }

                        Encoder<LevelConfigurationInterface<?>> encoder = (Encoder<LevelConfigurationInterface<?>>) currentConfig.codec().encoder();
                        var tag = encoder.encode(currentConfig, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();

                        var location = c.registry().entrySet().stream()
                                .filter(e -> e.getValue().command() == currentConfig.command())
                                .findAny()
                                .map(e -> e.getKey().location())
                                .orElseThrow();

                        i.getSource().sendSuccess(() -> Component.literal(location.toString()).append(Component.literal(": ")).append(NbtUtils.toPrettyComponent(tag)), true);
                        return 1;
                    }));
        }

        return result;
    }
}
