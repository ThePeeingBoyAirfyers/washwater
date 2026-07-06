package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionFactory;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeInfo;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.SingleFluidSection;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class DefaultFluidSectionFactory implements FluidSectionFactory {
    @Override
    public FluidSection upgrade(FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info) {
        FluidSection result;
        if (info instanceof SingleFluidUpgradeInfo(FluidType fluidType)) {
            result = new SingleFluidSection(fluidType);
        } else if (info == EnumUpgradeInfo.MULTIPLE) {
            result = new DumbFluidSection();
        } else throw new IllegalArgumentException("Unknown upgrade info: " + info);

        result.copyFrom(currentFluidSection);
        return result;
    }
}
