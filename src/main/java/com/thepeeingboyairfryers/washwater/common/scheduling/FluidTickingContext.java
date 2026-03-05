package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import it.unimi.dsi.fastutil.longs.LongSet;

public interface FluidTickingContext {

    void tickFluid(FluidRegion region, int x, int y, int z, MultiFluidValue value, int random);

    void submitTickSet(LongSet toBeTicked);
}
