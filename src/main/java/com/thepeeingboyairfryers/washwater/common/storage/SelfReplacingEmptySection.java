package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SelfReplacingEmptySection implements FluidSection {
    private FluidSectionContainer container;
    private FluidSection otherSection = null;


    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        if (otherSection == null) {
            otherSection = defaultSection();
            container.update(otherSection);
        }
        synchronized (otherSection) {
            otherSection.setVolume(x, y, z, fluids);
        }
    }

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        if (otherSection == null) return 0;
        synchronized (otherSection) {
            return otherSection.getVolumeOf(x, y, z, type);
        }
    }

    @Override
    public @NotNull MultiFluidValue getVolume(int x, int y, int z) {
        if (otherSection == null) return MultiFluidValue.EMPTY;
        synchronized (otherSection) {
            return otherSection.getVolume(x, y, z);
        }
    }

    @Override
    public short getAllVolume(int x, int y, int z) {
        if (otherSection == null) return 0;
        synchronized (otherSection) {
            return otherSection.getAllVolume(x, y, z);
        }
    }

    @Override
    public boolean isEmpty() {
        if (otherSection == null) return true;
        synchronized (otherSection) {
            return otherSection.isEmpty();
        }
    }

    @Override
    public void setContainer(FluidSectionContainer iContainer) {
        if (otherSection == null)
            container = iContainer;
        else synchronized (otherSection) {
            otherSection.setContainer(iContainer);
        }
    }

    @Override
    public MapCodec<? extends FluidSection> codec() {
        if (otherSection == null) return FluidSection.EMPTY.codec();
        return otherSection.codec();
    }

    private static FluidSection defaultSection() {
        return new DumbFluidSection();
    }
}
