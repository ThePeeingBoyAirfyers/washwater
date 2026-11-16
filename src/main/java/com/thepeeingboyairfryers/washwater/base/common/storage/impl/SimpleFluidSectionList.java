package com.thepeeingboyairfryers.washwater.base.common.storage.impl;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SimpleFluidSectionList implements FluidSectionList {
    private final List<FluidSection> list;

    public SimpleFluidSectionList(int sectionsCount) {
        list = new ArrayList<>();
        for (int i = 0; i < sectionsCount; i++) {
            list.add(FluidSection.EMPTY);
        }
    }

    public SimpleFluidSectionList(List<FluidSection> iSections) {
        list = new ArrayList<>(iSections);
    }

    @Override
    public @NotNull FluidSection get(int i) {
        return list.get(i);
    }

    @Override
    public void set(int i, @NotNull FluidSection fluidSection) {
        list.set(i, fluidSection);
    }

    @Override
    public List<FluidSection> getListCopy() {
        return List.copyOf(list);
    }

    @Override
    public Iterator<FluidSection> iterator() {
        return list.iterator();
    }
}
