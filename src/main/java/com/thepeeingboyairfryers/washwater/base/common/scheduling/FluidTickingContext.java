package com.thepeeingboyairfryers.washwater.base.common.scheduling;

import com.thepeeingboyairfryers.washwater.base.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;

public interface FluidTickingContext {

    void tickFluid(FluidRegion region, int x, int y, int z, MultiFluidValue value, int random);

    TickTracker makeTickTracker(TickTracker prevTickTracker, int sX, int sY, int sZ, LocalPosSet nextTickInSection);

    void queueTask(FluidTickingTask task);
}
