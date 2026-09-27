package com.thepeeingboyairfryers.washwater.base.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public class LevelMeta {
    //TODO make this not use directly a level
    private final Level level;

    public LevelMeta(Level iLevel) {
        this.level = iLevel;
    }

    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return level.getBlockEntity(blockPos);
    }

    public int getHeight() {
        return level.getHeight();
    }

    public int getMinBuildHeight() {
        return level.getMinBuildHeight();
    }
}
