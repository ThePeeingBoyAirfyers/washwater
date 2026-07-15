package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeStrategy;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeInfo;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import org.jetbrains.annotations.NotNull;

public class DumbFluidSectionUpgradeStrategy implements FluidSectionUpgradeStrategy {
    public static final DumbFluidSectionUpgradeStrategy INSTANCE = new DumbFluidSectionUpgradeStrategy();

    @Override
    public FluidSection upgrade(FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info) {
        var result = new DumbFluidSection();

        if (currentFluidSection != null)
            result.copyFrom(currentFluidSection);

        return result;
    }

    @Override
    public MapCodec<DumbFluidSectionUpgradeStrategy> codec() {
        return MapCodec.unit(INSTANCE);
    }

    @Override
    public ConfigurationCommand<DumbFluidSectionUpgradeStrategy> command() {
        return (ctx, data) -> INSTANCE;
    }
}
