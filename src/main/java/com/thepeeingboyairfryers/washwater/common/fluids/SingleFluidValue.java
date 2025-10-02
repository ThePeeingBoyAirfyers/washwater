package com.thepeeingboyairfryers.washwater.common.fluids;

import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;

public class SingleFluidValue implements MultiFluidValue {
    private final Entry entry;
    SingleFluidValue(FluidType fluidTyp, short vol) {
        this.entry = new Entry(vol, fluidTyp);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public short forFluid(FluidType type) {
        if (entry.fluidType().equals(type)) {
            return entry.volume();
        } else return 0;
    }

    @Override
    public @NotNull Iterator<Entry> iterator() {
        return Collections.singleton(entry).iterator();
    }

    public FluidType getFluidType() {
        return entry.fluidType();
    }

    @Override
    public short getTotalVolume() {
        return entry.volume();
    }

    public short getVolume() {
        return entry.volume();
    }
}
