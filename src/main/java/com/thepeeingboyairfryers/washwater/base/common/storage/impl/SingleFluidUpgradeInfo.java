package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeInfo;
import net.neoforged.neoforge.fluids.FluidType;

public record SingleFluidUpgradeInfo(FluidType fluidType) implements FluidSectionUpgradeInfo {

}
