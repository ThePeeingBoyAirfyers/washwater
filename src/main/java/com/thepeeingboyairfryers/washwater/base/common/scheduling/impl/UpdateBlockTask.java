package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickingTask;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;

public record UpdateBlockTask(int x, int y, int z, BlockState oldState, BlockState newState) implements FluidTickingTask.MainThreadTask {
    private static final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    @Override
    public void run(ServerLevel level) {
        level.sendBlockUpdated(pos.set(x, y, z), oldState, newState, 3);
    }
}
