package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import net.minecraft.server.level.ServerLevel;

public interface FluidTickingTask {

    interface MainThreadTask extends FluidTickingTask {
        void run(ServerLevel level);
    }
}
