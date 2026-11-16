package com.thepeeingboyairfryers.washwater.base.common.storage;

import javax.annotation.Nonnull;
import java.util.List;

public interface FluidSectionList extends Iterable<FluidSection> {
    @Nonnull FluidSection get(int i);
    void set(int i, @Nonnull FluidSection fluidSection);

    List<FluidSection> getListCopy();
}
