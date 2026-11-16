package com.thepeeingboyairfryers.washwater.util.poker;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PokedFluidSectionList implements FluidSectionList {
    private final ArrayList<FluidSection> pokedSections = new ArrayList<>();

    public PokedFluidSectionList(FluidSectionList iPoked) {
        var iter = iPoked.iterator();
        for (int i = 0; iter.hasNext(); iter.next()) {
            pokedSections.add(new PokedFluidSection(i++, iPoked));
        }
    }

    @Override
    public @NotNull FluidSection get(int i) {
        return pokedSections.get(i);
    }

    @Override
    public void set(int i, @NotNull FluidSection fluidSection) {
        // poked.set(i, fluidSection);
    }

    @Override
    public List<FluidSection> getListCopy() {
        return List.copyOf(pokedSections);
    }

    @Override
    public Iterator<FluidSection> iterator() {
        return pokedSections.iterator();
    }
}
