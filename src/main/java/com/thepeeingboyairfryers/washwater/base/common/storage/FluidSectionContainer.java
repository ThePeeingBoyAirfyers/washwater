package com.thepeeingboyairfryers.washwater.base.common.storage;

public interface FluidSectionContainer {
    FluidSection upgrade(FluidSection section, FluidSectionUpgradeInfo info);

    void markDirty();
}
