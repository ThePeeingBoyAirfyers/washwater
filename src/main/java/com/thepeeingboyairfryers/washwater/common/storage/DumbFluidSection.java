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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DumbFluidSection extends UpgradeableFluidSection {
    public static final MapCodec<Short2ObjectMap.Entry<MultiFluidValue>> ENTRY_CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            Codec.SHORT.fieldOf("pos").forGetter(Short2ObjectMap.Entry::getShortKey),
            MultiFluidValue.CODEC.fieldOf("value").forGetter(Short2ObjectMap.Entry<MultiFluidValue>::getValue)
    ).apply(b, AbstractShort2ObjectMap.BasicEntry::new));
    public static final MapCodec<DumbFluidSection> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            Codec.list(ENTRY_CODEC.codec()).fieldOf("fluids").forGetter(s -> new ArrayList<>(s.map.short2ObjectEntrySet()))
    ).apply(b, DumbFluidSection::new));

    private final Short2ObjectMap<MultiFluidValue> map = new Short2ObjectAVLTreeMap<>();
    private final ShortList dirty = new ShortArrayList();

    public DumbFluidSection() {
    }

    public DumbFluidSection(List<Short2ObjectMap.Entry<MultiFluidValue>> iMap) {
        for (var e : iMap) {
            this.map.put(e.getShortKey(), e.getValue());
        }

        this.dirty.addAll(this.map.keySet());
    }

    @Override
    public void volume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        short p = FluidSection.localPos2Short(x, y, z);
        if (fluids.isEmpty()) {
            if (map.remove(p) != null) {
                dirty.add(p);
            }
            return;
        }

        map.put(p, fluids);
        dirty.add(p);
        markDirty();
    }

    @Override
    public @NotNull MultiFluidValue volume(int x, int y, int z) {
        return map.getOrDefault(FluidSection.localPos2Short(x, y, z), MultiFluidValue.EMPTY);
    }

    @Override
    protected boolean empty() {
        return map.isEmpty();
    }

    @Override
    public @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        if (dirty.isEmpty() && !all) return null;
        var updates = (all ? map.keySet() : dirty).stream().map(s -> Pair.of(s, map.getOrDefault(s, MultiFluidValue.EMPTY))).toList();
        if (!all) dirty.clear();
        return new DumbFluidSectionUpdatePacket(pos, updates);
    }

    @Override
    protected @NotNull MapCodec<? extends FluidSection> myCodec() {
        return CODEC;
    }

    public Stream<BlockPos> allKeys() {
        return map.keySet().stream().map(FluidSection::short2localPos);
    }
}
