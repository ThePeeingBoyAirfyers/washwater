package com.thepeeingboyairfryers.washwater.base.common.fluids;

import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

public class SingleFluidValue implements MultiFluidValue {
    private final Entry entry;

    SingleFluidValue(@NotNull FluidType fluidTyp, short vol) {
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
    public short forFluid(@NotNull FluidType type) {
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

    @Override
    public @NotNull MultiFluidValue setFluid(FluidType type, short value) {
        if (entry.fluidType().equals(type)) {
            return new SingleFluidValue(type, value);
        }

        return this; //TODO multifluids
    }

    public short getVolume() {
        return entry.volume();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SingleFluidValue entries = (SingleFluidValue) o;
        return Objects.equals(entry, entries.entry);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entry);
    }
}
