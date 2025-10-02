package com.thepeeingboyairfryers.washwater.duck;

import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface IChunkFluidSection extends FluidSectionContainer {

    @Override
    default void update(FluidSection section) {
        ww€setFluidSection(section);
    }

    @Nullable FluidSection ww€getFluidSection();

    void ww€setFluidSection(@Nullable FluidSection fSection);

    void ww€configureFluidSectionUpdater(@NotNull Consumer<FluidSection> updater);
}
