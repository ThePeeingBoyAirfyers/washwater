package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradeableFluidSection implements FluidSection {
    private FluidSectionContainer container;
    private FluidSection otherSection = null;
    private boolean isAcquired = false;
    private boolean acqDirty = false;

    protected void upgrade(@NotNull FluidSection newSection) {
        otherSection = newSection;
        if (isAcquired) otherSection.acquire();
        container.update(otherSection);
    }

    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        if (otherSection == null) {
            volume(x, y, z, fluids);
        } else otherSection.setVolume(x, y, z, fluids);
    }

    protected abstract void volume(int x, int y, int z, @NotNull MultiFluidValue fluids);

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        if (otherSection == null) {
            return volumeOf(x, y, z, type);
        }

        return otherSection.getVolumeOf(x, y, z, type);
    }

    // Replace for faster impl
    protected short volumeOf(int x, int y, int z, FluidType type) {
        return volume(x, y, z).forFluid(type);
    }

    @Override
    public short getAllVolume(int x, int y, int z) {
        if (otherSection == null) {
            return allVolume(x, y, z);
        }

        return otherSection.getAllVolume(x, y, z);
    }

    // Replace for faster impl
    protected short allVolume(int x, int y, int z) {
        return volume(x, y, z).getTotalVolume();
    }

    @Override
    public @NotNull MultiFluidValue getVolume(int x, int y, int z) {
        if (otherSection == null) {
            return volume(x, y, z);
        }

        return otherSection.getVolume(x, y, z);
    }

    protected abstract @NotNull MultiFluidValue volume(int x, int y, int z);

    @Override
    public boolean isEmpty() {
        if (otherSection == null) {
            return empty();
        }

        return otherSection.isEmpty();
    }

    protected abstract boolean empty();

    @Override
    public void setContainer(@NotNull FluidSectionContainer iContainer) {
        if (otherSection == null) {
            container = iContainer;
        } else {
            otherSection.setContainer(iContainer);
        }
    }

    protected void markDirty() {
        if (!isAcquired)
            container.markDirty();
        else acqDirty = true;
    }

    @Override
    public @Nullable CustomPacketPayload buildUpdatePacket(SectionPos pos, boolean all) {
        if (otherSection == null) {
            return updatePacket(pos, all);
        }

        return otherSection.buildUpdatePacket(pos, all);
    }

    protected abstract @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all);

    @Override
    public MapCodec<? extends FluidSection> codec() {
        if (otherSection == null) return myCodec();
        return otherSection.codec();
    }

    protected abstract @NotNull MapCodec<? extends FluidSection> myCodec();

    @Override
    public void acquire() {
        if (otherSection != null) {
            otherSection.acquire();
            return;
        }

        if (isAcquired) throw new IllegalStateException("Already acquired");
        isAcquired = true;
        acqDirty = false;
    }

    @Override
    public void release() {
        if (otherSection != null) {
            otherSection.release();
            return;
        }

        if (!isAcquired) throw new IllegalStateException("Cannot release a non-acquired fluid section");
        isAcquired = false;
        if (acqDirty)
            container.markDirty();
        // else WashWater.LOGGER.info("Not dirty!");
    }
}
