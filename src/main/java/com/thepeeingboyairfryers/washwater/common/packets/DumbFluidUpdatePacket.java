package com.thepeeingboyairfryers.washwater.common.packets;


import com.mojang.datafixers.util.Pair;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.util.WWStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public class DumbFluidUpdatePacket extends SectionUpdatePacket {
    public static final CustomPacketPayload.Type<DumbFluidUpdatePacket> TYPE = newType("dumb_fluid_update");
    public static final StreamCodec<ByteBuf, DumbFluidUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    WWStreamCodecs.SECTION_POS,
                    DumbFluidUpdatePacket::getPos,
                    ByteBufCodecs.collection(ArrayList::new, WWStreamCodecs.pair(ByteBufCodecs.SHORT, MultiFluidValue.STREAM_CODEC)),
                    DumbFluidUpdatePacket::getUpdates,
                    DumbFluidUpdatePacket::new
            );

    private final SectionPos pos;
    private final List<Pair<Short, MultiFluidValue>> updates;

    public DumbFluidUpdatePacket(SectionPos iPos, List<Pair<Short, MultiFluidValue>> iUpdates) {
        this.pos = iPos;
        this.updates = iUpdates;
    }

    @Override
    public SectionPos getPos() {
        return pos;
    }

    public List<Pair<Short, MultiFluidValue>> getUpdates() {
        return updates;
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(FluidSection section) {
        for (var u : updates) {
            var x = FluidSection.short2localX(u.getFirst());
            var y = FluidSection.short2localY(u.getFirst());
            var z = FluidSection.short2localZ(u.getFirst());
            section.setVolume(x, y, z, u.getSecond());
        }
    }
}
