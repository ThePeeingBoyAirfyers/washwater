package com.thepeeingboyairfryers.washwater.common.storage.attachment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.packets.WWNetworking;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.duck.IChunkFluidSection;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class FluidChunkAttachment implements Iterable<FluidSection> {
    public static final MapCodec<FluidChunkAttachment> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidSection.CODEC.listOf().fieldOf("sections")
                    .forGetter(FluidChunkAttachment::getSections)
    ).apply(i, FluidChunkAttachment::new));

    private List<FluidSection> sections = null;
    private LevelChunk chunk;

    public FluidChunkAttachment() {
    }

    private FluidChunkAttachment(List<FluidSection> iSections) {
        this.sections = new ArrayList<>(iSections);
    }

    public void configure(LevelChunk iChunk) {
        if (chunk == iChunk) return;
        this.chunk = iChunk;

        if (sections == null) {
            sections = new ArrayList<>(chunk.getSectionsCount());
            for (int i = 0; i < chunk.getSectionsCount(); i++) {
                sections.add(FluidSection.EMPTY);
            }
        }

        for (int i = 0; i < chunk.getSectionsCount(); i++) {
            FluidSection section = sections.get(i);
            section.acquireWriteLock();
            try {
                IChunkFluidSection container = ((IChunkFluidSection) chunk.getSection(i));
                container.ww€configureFluidSectionUpdater(new SectionUpdater(i));
                container.ww€setFluidSection(section);
            } finally {
                section.releaseWriteLock();
            }
        }
    }

    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        section.acquireWriteLock();
        try {
            section.setVolume(x & 15, y & 15, z & 15, value);
        } finally {
            section.releaseWriteLock();
        }
    }

    public short getVolume(int x, int y, int z, FluidType type) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        section.acquireReadLock();
        try {
            return section.getVolumeOf(x & 15, y & 15, z & 15, type);
        } finally {
            section.releaseReadLock();
        }
    }

    public short getAllVolume(int x, int y, int z) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        section.acquireReadLock();
        try {
            return section.getAllVolume(x & 15, y & 15, z & 15);
        } finally {
            section.releaseReadLock();
        }
    }

    public void addFluidVolume(int x, int y, int z, FluidType type, short volume) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        section.acquireWriteLock();
        try {
            var values = section.getVolume(x & 15, y & 15, z & 15);
            short total = 0;
            for (MultiFluidValue.Entry e : values) {
                total += e.volume();
            }
            if (total >= FluidUtil.VOLUME_OF_BLOCK) return;
            volume = (short) Math.min(volume, FluidUtil.VOLUME_OF_BLOCK - total);

            section.setVolume(
                    x & 15, y & 15, z & 15,
                    values.setFluid(type, (short) (volume + values.forFluid(type)))
            );
        } finally {
            section.releaseWriteLock();
        }
    }

    public FluidSection getSectionWithY(int y) {
        if (y < chunk.getMinSection()) return null;
        return sections.get(chunk.getSectionIndexFromSectionY(y));
    }

    private List<FluidSection> getSections() {
        return sections;
    }

    @Override
    public @NotNull Iterator<FluidSection> iterator() {
        return sections.iterator();
    }

    public class SectionUpdater implements Consumer<FluidSection> {
        private final int i;
        private AtomicBoolean isStillDirty = new AtomicBoolean(false);

        private SectionUpdater(int ii) {
            this.i = ii;
        }

        @Override
        public void accept(FluidSection fluidSection) {
            sections.set(i, fluidSection);
        }

        public void markDirty() {
            if (!isStillDirty.compareAndExchange(false, true)) return;

            int x = chunk.getPos().x;
            int y = chunk.getMinSection() + i;
            int z = chunk.getPos().z;

            if (chunk.getLevel().isClientSide) {
                // TODO no feedback mechanism
                Minecraft.getInstance().levelRenderer.setSectionDirty(x, y, z);
                return;
            }

            chunk.setUnsaved(true);
            WWNetworking.queueUpdate((ServerLevel) chunk.getLevel(), x, y, z).whenComplete((v, t) -> isStillDirty.compareAndSet(true, false));
        }
    }
}
