package com.thepeeingboyairfryers.washwater.base.common.scheduling.impl;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.flow.FluidRegion;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTickSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Fixes waterfall clumping, seperated out cus its a wierd fix, we should look into making this more sane?
 */
public class WaterfallFix {
    private final FluidTickSection section;
    private final List<FluidAddOperation> operations = new ArrayList<>();

    public WaterfallFix(FluidTickSection iSection) {
        this.section = iSection;
    }

    public void addLate(FluidRegion owner, int x, int y, int z, MultiFluidValue fluid) {
        operations.add(new FluidAddOperation(x, y, z, fluid));
        ((FluidTickSection) owner).toBeTicked(x, y, z);
    }

    public void apply() {
        for (var opr : operations) {
            MultiFluidValue fluids = section.getFluids(opr.x, opr.y, opr.z);
            if (fluids.getTotalVolume() + opr.fluid().getTotalVolume() > WaterInfo.VOLUME_PER_BLOCK)
                throw new IllegalStateException();

            section.setVolume(opr.x, opr.y, opr.z, fluids.add(opr.fluid));
        }
    }

    private record FluidAddOperation(int x, int y, int z, MultiFluidValue fluid) { }
}
