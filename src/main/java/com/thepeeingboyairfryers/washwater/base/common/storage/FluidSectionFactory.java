package com.thepeeingboyairfryers.washwater.base.common.storage;

import org.jetbrains.annotations.NotNull;

public interface FluidSectionFactory {
    FluidSection upgrade(FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info);
}
