package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.packets.DumbFluidSectionUpdatePacket;
import it.unimi.dsi.fastutil.shorts.AbstractShort2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DumbFluidSection implements FluidSection {
    public static final MapCodec<Short2ObjectMap.Entry<MultiFluidValue>> ENTRY_CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            Codec.SHORT.fieldOf("pos").forGetter(Short2ObjectMap.Entry::getShortKey),
            MultiFluidValue.CODEC.fieldOf("value").forGetter(Short2ObjectMap.Entry<MultiFluidValue>::getValue)
    ).apply(b, AbstractShort2ObjectMap.BasicEntry::new));
    public static final MapCodec<DumbFluidSection> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            Codec.list(ENTRY_CODEC.codec()).fieldOf("fluids").forGetter(s -> new ArrayList<>(s.map.short2ObjectEntrySet()))
    ).apply(b, DumbFluidSection::new));
    private final Short2ObjectMap<MultiFluidValue> map = new Short2ObjectAVLTreeMap<>();
    private final ShortList dirty = new ShortArrayList();

    public DumbFluidSection() { }
    public DumbFluidSection(List<Short2ObjectMap.Entry<MultiFluidValue>> iMap) {
        for (var e : iMap) {
            this.map.put(e.getShortKey(), e.getValue());
        }

        this.dirty.addAll(this.map.keySet());
    }

    @Override
    public void setVolume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        short p = FluidSection.localPos2Short(x, y, z);
        if (fluids.isEmpty()) {
            if (map.remove(p) != null) {
                dirty.add(p);
            }
            return;
        }

        map.put(p, fluids);
        dirty.add(p);
    }

    @Override
    public short getVolumeOf(int x, int y, int z, FluidType type) {
        return getVolume(x, y, z).forFluid(type);
    }

    @Override
    public @NotNull MultiFluidValue getVolume(int x, int y, int z) {
        return map.getOrDefault(FluidSection.localPos2Short(x, y, z), MultiFluidValue.EMPTY);
    }

    @Override
    public short getAllVolume(int x, int y, int z) {
        MultiFluidValue v =  getVolume(x, y, z);
        short total = 0;

        for (MultiFluidValue.Entry e : v) {
            total += e.volume();
        }

        return total;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public void setContainer(@NotNull FluidSectionContainer container) {
        // No operation, as this is a dumb section.
    }

    @Override
    public @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        if (dirty.isEmpty() && !all) return null;
        var updates = (all ? map.keySet() : dirty).stream().map(s -> Pair.of(s, map.get(s))).toList();
        if (!all) dirty.clear();
        return new DumbFluidSectionUpdatePacket(pos, updates);
    }

    @Override
    public MapCodec<DumbFluidSection> codec() {
        return CODEC;
    }

    public Stream<BlockPos> allKeys() {
        return map.keySet().stream().map(FluidSection::short2localPos);
    }
}
