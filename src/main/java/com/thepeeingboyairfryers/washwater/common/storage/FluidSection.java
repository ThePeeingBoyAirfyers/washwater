package com.thepeeingboyairfryers.washwater.common.storage;

import com.thepeeingboyairfryers.washwater.common.util.MultiFluidResult;
import net.neoforged.neoforge.fluids.FluidType;

public interface FluidSection {
    void setVolume(int x, int y, int z, FluidType type, short volume);
    short getVolumeOf(int x, int y, int z, FluidType type);

    MultiFluidResult getVolume(int x, int y, int z);

    boolean isEmpty();

    void setContainer(FluidSectionContainer container);
}
