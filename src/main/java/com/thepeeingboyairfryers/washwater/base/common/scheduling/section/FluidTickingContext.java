package com.thepeeingboyairfryers.washwater.base.common.scheduling.section;

import com.thepeeingboyairfryers.washwater.base.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.world.level.block.state.BlockState;

public interface FluidTickingContext {

    void tickFluid(FluidRegion region, int x, int y, int z, MultiFluidValue value, int random);

    TickTracker makeTickTracker(TickTracker prevTickTracker, int sX, int sY, int sZ, ShortSet nextTickInSection);

    void updateBlock(int x, int y, int z, BlockState oldState, BlockState newState);
}
