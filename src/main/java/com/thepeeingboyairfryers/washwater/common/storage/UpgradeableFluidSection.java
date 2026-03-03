package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.util.parallel.DebugThreadDetector;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class UpgradeableFluidSection implements FluidSection {
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final DebugThreadDetector debug = DebugThreadDetector.newInstance();
    private FluidSectionContainer container;
    private FluidSection otherSection = null;

    protected void upgrade(@NotNull FluidSection newSection) {
        if (!lock.isWriteLockedByCurrentThread())
            throw new IllegalStateException("Original lock not acquired when trying to upgrade?");

        otherSection = newSection;
        otherSection.acquireWriteLock();
        container.update(otherSection);

        lock.writeLock().unlock(); // Everyone waiting can now continue and wait again
    }

    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        if (otherSection == null) {
            assert lock.isWriteLockedByCurrentThread();
            volume(x, y, z, fluids);
        } else otherSection.setVolume(x, y, z, fluids);
    }

    protected abstract void volume(int x, int y, int z, @NotNull MultiFluidValue fluids);

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        if (otherSection == null) {
            assert lock.getReadLockCount() > 0  || lock.isWriteLockedByCurrentThread();
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
            assert lock.getReadLockCount() > 0 || lock.isWriteLockedByCurrentThread();
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
            assert lock.getReadLockCount() > 0 || lock.isWriteLockedByCurrentThread();
            return volume(x, y, z);
        }
        return otherSection.getVolume(x, y, z);
    }

    protected abstract @NotNull MultiFluidValue volume(int x, int y, int z);

    @Override
    public boolean isEmpty() {
        if (otherSection == null) {
            assert lock.getReadLockCount() > 0 || lock.isWriteLockedByCurrentThread();
            return empty();
        }

        return otherSection.isEmpty();
    }

    protected abstract boolean empty();

    @Override
    public void setContainer(@NotNull FluidSectionContainer iContainer) {
        if (otherSection == null) {
            assert lock.isWriteLockedByCurrentThread();
            container = iContainer;
        } else {
            otherSection.setContainer(iContainer);
        }
    }

    protected void markDirty() {
        container.markDirty();
    }

    @Override
    public @Nullable CustomPacketPayload buildUpdatePacket(SectionPos pos, boolean all) {
        if (otherSection == null) {
            assert (!all && lock.isWriteLockedByCurrentThread())
                    || (all && (lock.getReadLockCount() > 0 || lock.isWriteLockedByCurrentThread()));
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
    public void acquireWriteLock() {
        if (otherSection == null) {
            debug.acquire();
            lock.writeLock().lock();
            if (otherSection != null)
                otherSection.acquireWriteLock();
        } else otherSection.acquireWriteLock();
    }

    @Override
    public void acquireReadLock() {
        if (otherSection == null) {
            lock.readLock().lock();
            if (otherSection != null)
                otherSection.acquireReadLock();
        } else otherSection.acquireReadLock();
    }

    @Override
    public void releaseWriteLock() {
        if (otherSection == null) {
            debug.release();
            lock.writeLock().unlock();
        } else otherSection.releaseWriteLock();
    }

    @Override
    public void releaseReadLock() {
        if (otherSection == null) {
            lock.readLock().unlock();
        } else otherSection.releaseReadLock();
    }
}
