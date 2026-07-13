package com.thepeeingboyairfryers.washwater.gameplay.common;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import static net.minecraft.commands.Commands.literal;

public class WWCommand {
    private WWCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static void registerCommand(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("ww")
                .then(stats()));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> stats() {
        return literal("stats").executes((ctx -> {
            ctx.getSource().sendSuccess(WWStats::printReport, true);
            return 0;
        }));
    }
}
