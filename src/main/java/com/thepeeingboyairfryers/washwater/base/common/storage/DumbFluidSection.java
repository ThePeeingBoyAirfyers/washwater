package com.thepeeingboyairfryers.washwater.base.common.storage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.packets.DumbFluidUpdatePacket;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class DumbFluidSection extends UpgradeableFluidSection {
    public static final MapCodec<DumbFluidSection> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            MultiFluidValue.CODEC.listOf().fieldOf("array").forGetter(s -> Arrays.asList(s.array)),
            Codec.INT.fieldOf("nonEmpty").forGetter(DumbFluidSection::getNonEmpty)
    ).apply(b, (list, nonEmpty) -> new DumbFluidSection(list.toArray(new MultiFluidValue[16 * 16 * 16]), nonEmpty)));

    private final MultiFluidValue[] array;
    private final ShortSet dirty = new ShortRBTreeSet();
    private int nonEmpty;

    public DumbFluidSection() {
        this(new MultiFluidValue[16 * 16 * 16], 0);
        Arrays.fill(array, MultiFluidValue.EMPTY);
    }

    public DumbFluidSection(MultiFluidValue[] iArray, int iNonEmpty) {
        array = iArray;
        nonEmpty = iNonEmpty;
    }

    @Override
    public void volume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        short p = FluidSection.localPos2Short(x, y, z);
        var prev = array[p];
        if (fluids.isEmpty() && !prev.isEmpty()) {
            nonEmpty--;
        } else if (!fluids.isEmpty() && prev.isEmpty()) nonEmpty++;

        array[p] = fluids;
        dirty.add(p);
        markDirty();
    }

    @Override
    public @NotNull MultiFluidValue volume(int x, int y, int z) {
        return array[FluidSection.localPos2Short(x, y, z)];
    }

    @Override
    protected boolean empty() {
        return nonEmpty == 0;
    }

    @Override
    public @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        if (dirty.isEmpty() && !all) return null;
        var updates = new ArrayList<Pair<Short, MultiFluidValue>>();
        if (!all) {
            updates.ensureCapacity(dirty.size());
            for (short s : dirty) {
                updates.add(Pair.of(s, array[s]));
            }
            dirty.clear();
        } else {
            for (short i = 0; i < array.length; i++) {
                updates.add(Pair.of(i, array[i]));
            }
        }
        return new DumbFluidUpdatePacket(pos, updates);
    }

    @Override
    protected @NotNull MapCodec<? extends FluidSection> myCodec() {
        return CODEC;
    }

    @Override
    protected void copy(FluidSection section) {
        section.fill(array);
    }

    private int getNonEmpty() {
        return nonEmpty;
    }
}
