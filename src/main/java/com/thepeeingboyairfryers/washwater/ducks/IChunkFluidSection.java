package com.thepeeingboyairfryers.washwater.ducks;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionContainer;
import com.thepeeingboyairfryers.washwater.base.common.storage.attachment.FluidChunkAttachment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IChunkFluidSection extends FluidSectionContainer {

    @Nullable FluidSection ww€getFluidSection();

    void ww€setFluidSection(@Nullable FluidSection fSection);

    void ww€configureFluidSectionUpdater(@NotNull FluidChunkAttachment.SectionUpdater updater, boolean isWorldGen);
}
