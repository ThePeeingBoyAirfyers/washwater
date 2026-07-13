package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeStrategy;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionUpgradeInfo;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.SelfReplacingEmptySection;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.SingleFluidSection;
import com.thepeeingboyairfryers.washwater.util.registry.ConfigurationCommand;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultFluidSectionUpgradeStrategy implements FluidSectionUpgradeStrategy {
    public static final DefaultFluidSectionUpgradeStrategy INSTANCE = new DefaultFluidSectionUpgradeStrategy();

    @Override
    public FluidSection upgrade(@Nullable FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info) {
        FluidSection result;
        if (info == EnumUpgradeInfo.EMPTY) {
            result = new SelfReplacingEmptySection();
        } else if (info instanceof SingleFluidUpgradeInfo(FluidType fluidType)) {
            result = new SingleFluidSection(fluidType);
        } else if (info == EnumUpgradeInfo.MULTIPLE) {
            result = new DumbFluidSection();
        } else throw new IllegalArgumentException("Unknown upgrade info: " + info);

        if (currentFluidSection != null)
            result.copyFrom(currentFluidSection);

        return result;
    }

    @Override
    public MapCodec<DefaultFluidSectionUpgradeStrategy> codec() {
        return MapCodec.unit(INSTANCE);
    }

    @Override
    public ConfigurationCommand<DefaultFluidSectionUpgradeStrategy> command() {
        return (ctx, data) -> INSTANCE;
    }
}
