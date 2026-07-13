package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionFactory;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeInfo;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.DumbFluidSection;
import org.jetbrains.annotations.NotNull;

public class DumbFluidSectionFactory implements FluidSectionFactory {
    @Override
    public FluidSection upgrade(FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info) {
        var result = new DumbFluidSection();
        result.copyFrom(currentFluidSection);
        return result;
    }
}
