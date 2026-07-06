package com.thepeeingboyairfryers.washwater.base.common.storage.attachment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.collections.WWNetworking;
import com.thepeeingboyairfryers.washwater.ducks.IChunkFluidSection;
import com.thepeeingboyairfryers.washwater.util.parallel.MainThreads;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class FluidChunkAttachment implements Iterable<FluidSection> {
    public static final MapCodec<FluidChunkAttachment> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            FluidSection.CODEC.listOf().fieldOf("sections")
                    .forGetter(FluidChunkAttachment::getSections)
    ).apply(i, FluidChunkAttachment::new));

    private List<FluidSection> sections = null;
    private ChunkAccess chunk;

    public FluidChunkAttachment() {
    }

    private FluidChunkAttachment(List<FluidSection> iSections) {
        this.sections = new ArrayList<>(iSections);
    }

    public void configure(ChunkAccess iChunk) {
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
            IChunkFluidSection container = ((IChunkFluidSection) chunk.getSection(i));
            container.ww€configureFluidSectionUpdater(new SectionUpdater(i), chunk instanceof ProtoChunk);
            container.ww€setFluidSection(section);
        }
    }

    public void setVolume(int x, int y, int z, MultiFluidValue value) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        section.setVolume(x & 15, y & 15, z & 15, value);
    }

    public short getVolume(int x, int y, int z, FluidType type) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        return section.getVolumeOf(x & 15, y & 15, z & 15, type);
    }

    public short getAllVolume(int x, int y, int z) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        return section.getAllVolume(x & 15, y & 15, z & 15);
    }

    public MultiFluidValue getFluids(int x, int y, int z) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        return section.getFluids(x & 15, y & 15, z & 15);
    }

    public void addFluidVolume(int x, int y, int z, FluidType type, short volume) {
        FluidSection section = sections.get(chunk.getSectionIndex(y));
        var values = section.getFluids(x & 15, y & 15, z & 15);
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

        private SectionUpdater(int ii) {
            this.i = ii;
        }

        @Override
        public void accept(FluidSection fluidSection) {
            sections.set(i, fluidSection);
            markDirty();
        }

        public void markDirty() {
            chunk.setUnsaved(true);

            Level level = chunk.getLevel();
            if (level == null) return;

            int x = chunk.getPos().x;
            int y = chunk.getMinSection() + i;
            int z = chunk.getPos().z;

            if (level.isClientSide) {
                if (MainThreads.isRenderThread())
                    Minecraft.getInstance().levelRenderer.setSectionDirty(x, y, z);
                return;
            }

            WWNetworking.queueUpdate((ServerLevel) level, x, y, z);
        }
    }
}
