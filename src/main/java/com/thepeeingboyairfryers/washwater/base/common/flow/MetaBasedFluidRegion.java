package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.LevelMeta;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public abstract class MetaBasedFluidRegion implements FluidRegion {
    private final LevelMeta levelMeta;

    protected MetaBasedFluidRegion(LevelMeta iLevelMeta) {
        this.levelMeta = iLevelMeta;
    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        return levelMeta.getBlockEntity(blockPos);
    }

    @Override
    public int getHeight() {
        return levelMeta.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return levelMeta.getMinBuildHeight();
    }
}
