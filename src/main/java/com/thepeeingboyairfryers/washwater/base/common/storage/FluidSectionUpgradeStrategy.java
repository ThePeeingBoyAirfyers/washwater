package com.thepeeingboyairfryers.washwater.base.common.storage;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.DefaultFluidSectionUpgradeStrategy;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationInterface;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationRegistry;
import com.thepeeingboyairfryers.washwater.util.registry.LevelConfigurationSide;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface FluidSectionUpgradeStrategy extends LevelConfigurationInterface<FluidSectionUpgradeStrategy> {
    LevelConfigurationRegistry<FluidSectionUpgradeStrategy> REGISTRY =
            LevelConfigurationRegistry.create(WashWater.resource("upgrade_strategy"), LevelConfigurationSide.COMMON, DefaultFluidSectionUpgradeStrategy::new);

    FluidSection upgrade(@Nullable FluidSection currentFluidSection, @NotNull FluidSectionUpgradeInfo info);
}
