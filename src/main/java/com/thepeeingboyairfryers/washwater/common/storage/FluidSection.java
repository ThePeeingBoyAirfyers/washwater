package com.thepeeingboyairfryers.washwater.common.storage;

import com.thepeeingboyairfryers.washwater.common.util.MultiFluidResult;
import com.thepeeingboyairfryers.washwater.duck.FluidSectionContainer;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public interface FluidSection {


    void setVolume(int x, int y, int z, FluidType type, short volume);
    short getVolumeOf(int x, int y, int z, FluidType type);

    @NotNull MultiFluidResult getVolume(int x, int y, int z);

    boolean isEmpty();

    /**
     * Sets the container that this FluidSection belongs to.
     * This is used to allow the FluidSection to 'update' itself or change things in the container.
     *
     * @param container The container that this FluidSection belongs to.
     */
    void setContainer(FluidSectionContainer container);

    /**
     * An empty FluidSection that does nothing.
     * This is used to avoid null checks in the code.
     */
    FluidSection EMPTY = new FluidSection() {
        @Override
        public void setVolume(int x, int y, int z, FluidType type, short volume) {
            // No operation, as this is an empty section.
        }

        @Override
        public short getVolumeOf(int x, int y, int z, FluidType type) {
            return 0;
        }

        @Override
        public @NotNull MultiFluidResult getVolume(int x, int y, int z) {
            return MultiFluidResult.EMPTY;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void setContainer(FluidSectionContainer container) {
            // No operation, as this is an empty section.
        }
    };
}
