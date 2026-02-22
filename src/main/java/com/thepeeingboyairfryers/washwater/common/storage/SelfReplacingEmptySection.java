package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.util.DummyLock;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.Lock;

@SuppressWarnings("SynchronizeOnNonFinalField")
public class SelfReplacingEmptySection implements FluidSection {
    private FluidSectionContainer container;
    private FluidSection otherSection = null;

    private static FluidSection defaultSection(FluidSectionContainer container) {
        return new DumbFluidSection();
    }

    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        if (otherSection == null) {
            otherSection = defaultSection(container);
            otherSection.writeLock().lock();
            container.update(otherSection);
        }
        otherSection.setVolume(x, y, z, fluids);
    }

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        if (otherSection == null) return 0;
        return otherSection.getVolumeOf(x, y, z, type);
    }

    @Override
    public @NotNull MultiFluidValue getVolume(int x, int y, int z) {
        if (otherSection == null) return MultiFluidValue.EMPTY;
        return otherSection.getVolume(x, y, z);
    }

    @Override
    public short getAllVolume(int x, int y, int z) {
        if (otherSection == null) return 0;
        return otherSection.getAllVolume(x, y, z);
    }

    @Override
    public boolean isEmpty() {
        if (otherSection == null) return true;
        return otherSection.isEmpty();
    }

    @Override
    public void setContainer(@NotNull FluidSectionContainer iContainer) {
        if (otherSection == null)
            container = iContainer;
        else {
            otherSection.setContainer(iContainer);
        }
    }

    @Override
    public @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        if (otherSection == null) return null;
        return otherSection.updatePacket(pos, all);
    }

    @Override
    public MapCodec<? extends FluidSection> codec() {
        if (otherSection == null) return FluidSection.EMPTY.codec();
        return otherSection.codec();
    }

    @Override
    public Lock readLock() {
        if (otherSection == null) return DummyLock.INSTANCE;
        return otherSection.readLock();
    }

    @Override
    public Lock writeLock() {
        if (otherSection == null) return DummyLock.INSTANCE;
        return otherSection.writeLock();
    }
}
