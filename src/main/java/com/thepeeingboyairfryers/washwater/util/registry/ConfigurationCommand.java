package com.thepeeingboyairfryers.washwater.util.registry;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.Tag;

public interface ConfigurationCommand<T extends LevelConfigurationInterface<?>> {
    T onCommand(CommandContext<CommandSourceStack> context, Tag data) throws CommandSyntaxException;
}
