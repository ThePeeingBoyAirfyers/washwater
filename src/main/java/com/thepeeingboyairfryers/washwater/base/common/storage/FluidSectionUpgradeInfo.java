package com.thepeeingboyairfryers.washwater.base.common.storage;

import com.thepeeingboyairfryers.washwater.base.common.storage.impl.EnumUpgradeInfo;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.SingleFluidUpgradeInfo;
import net.neoforged.neoforge.fluids.FluidType;

public interface FluidSectionUpgradeInfo {

    static FluidSectionUpgradeInfo multiple(int amount) {
        return EnumUpgradeInfo.MULTIPLE;
    }

    static FluidSectionUpgradeInfo single(FluidType type) {
        return new SingleFluidUpgradeInfo(type);
    }
}
