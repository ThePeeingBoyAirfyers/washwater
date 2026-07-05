package com.thepeeingboyairfryers.washwater.base.common.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


public interface FluidSection {
    ResourceKey<Registry<MapCodec<? extends FluidSection>>> DISPATCH_KEY = ResourceKey.createRegistryKey(WashWater.resource("fluid_section"));
    Registry<MapCodec<? extends FluidSection>> DISPATCH_REGISTRY = new RegistryBuilder<>(DISPATCH_KEY).create();
    Codec<FluidSection> CODEC = DISPATCH_REGISTRY.byNameCodec().dispatch(FluidSection::codec, Function.identity());

    static short localPos2Short(BlockPos pos) {
        return localPos2Short(pos.getX(), pos.getY(), pos.getZ());
    }

    static short localPos2Short(int x, int y, int z) {
        return (short) ((x & 15) | ((y & 15) << 4) | ((z & 15) << 8));
    }

    static BlockPos short2localPos(short v) {
        return new BlockPos(short2localX(v), short2localY(v), short2localZ(v));
    }

    static int short2localX(short v) {
        return v & 15;
    }

    static int short2localY(short v) {
        return (v >> 4) & 15;
    }

    static int short2localZ(short v) {
        return (v >> 8) & 15;
    }

    void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids);

    short getVolumeOf(int x, int y, int z, FluidType type);

    @NotNull MultiFluidValue getVolume(int x, int y, int z);

    short getAllVolume(int x, int y, int z);  // Has to be after EMPTY has been defined, dear god help this soul

    /**
     * An empty FluidSection that does nothing.
     * This is used to avoid null checks in the code.
     */
    FluidSection EMPTY = new FluidSection() {

        @Override
        public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
            // No operation, as this is an empty section.
        }

        @Override
        public short getVolumeOf(int x, int y, int z, FluidType type) {
            return 0;
        }

        @Override
        public @NotNull MultiFluidValue getVolume(int x, int y, int z) {
            return MultiFluidValue.EMPTY;
        }

        @Override
        public short getAllVolume(int x, int y, int z) {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public void setContainer(@NotNull FluidSectionContainer container) {
            var section = new SelfReplacingEmptySection();
            container.update(section);
        }

        @Override
        public void acquire() {

        }

        @Override
        public void release() {

        }

        @Override
        public @Nullable CustomPacketPayload buildUpdatePacket(SectionPos pos, boolean all) {
            return null;
        }

        @Override
        public MapCodec<FluidSection> codec() {
            return EMPTY_CODEC;
        }

        @Override
        public void copyFrom(FluidSection section) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return "FluidSection.EMPTY";
        }
    };

    boolean isEmpty();

    /**
     * Sets the container that this FluidSection belongs to.
     * This is used to allow the FluidSection to 'update' itself or change things in the container.
     *
     * @param container The container that this FluidSection belongs to.
     */
    void setContainer(@NotNull FluidSectionContainer container);    MapCodec<FluidSection> EMPTY_CODEC = MapCodec.unit(EMPTY);

    /**
     * Only use this offthread when the main thread is frozen.
     */
    void acquire();

    void release();

    /**
     * Returns a packet that contains the dirty data of this FluidSection.
     *
     * @param pos        The position of the section in the world.
     * @param fullUpdate If true, the packet should contain all data, not just the dirty data.
     *                   This is used when the chunk is sent to the client for the first time.
     * @return A packet that contains the dirty data of this FluidSection, or null if there is no dirty data.
     */
    @Nullable CustomPacketPayload buildUpdatePacket(SectionPos pos, boolean fullUpdate);

    MapCodec<? extends FluidSection> codec();

    void copyFrom(FluidSection section);

    default void fill(MultiFluidValue[] fluids) {
        if (fluids.length != 4096) throw new IllegalArgumentException();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    fluids[localPos2Short(x, y, z)] = getVolume(x, y, z);
                }
            }
        }
    }
}
