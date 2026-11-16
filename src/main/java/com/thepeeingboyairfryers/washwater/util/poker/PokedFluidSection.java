package com.thepeeingboyairfryers.washwater.util.poker;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionContainer;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionList;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PokedFluidSection implements FluidSection {
    private final int idx;
    private final FluidSectionList pokedList;

    public PokedFluidSection(int i, FluidSectionList iList) {
        this.idx = i;
        this.pokedList = iList;
    }

    private FluidSection getPoked() {
        return pokedList.get(idx);
    }


    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {

    }

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        return getPoked().getVolumeOf(x, y, z, type);
    }

    @Override
    public @NotNull MultiFluidValue getFluids(int x, int y, int z) {
        return getPoked().getFluids(x, y, z);
    }

    @Override
    public short getAllVolume(int x, int y, int z) {
        return getPoked().getAllVolume(x, y, z);
    }

    @Override
    public boolean isEmpty() {
        return getPoked().isEmpty();
    }

    @Override
    public void setContainer(@NotNull FluidSectionContainer container) {

    }

    @Override
    public void acquire() {

    }

    @Override
    public void release() {

    }

    @Override
    public @Nullable CustomPacketPayload buildUpdatePacket(SectionPos pos, boolean fullUpdate) {
        return null;
    }

    @Override
    public MapCodec<? extends FluidSection> codec() {
        return null;
    }

    @Override
    public void copyFrom(FluidSection section) {

    }
}
