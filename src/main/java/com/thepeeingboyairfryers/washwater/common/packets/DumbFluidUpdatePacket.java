package com.thepeeingboyairfryers.washwater.common.packets;


import com.mojang.datafixers.util.Pair;
import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.util.WWStreamCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.ArrayList;
import java.util.List;

public record DumbFluidUpdatePacket(
        SectionPos pos,
        List<Pair<Short, MultiFluidValue>> updates
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DumbFluidUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(WashWater.resource("dumb_fluid_update"));
    public static final StreamCodec<ByteBuf, DumbFluidUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    WWStreamCodecs.SECTION_POS,
                    DumbFluidUpdatePacket::pos,
                    ByteBufCodecs.collection(ArrayList::new, WWStreamCodecs.pair(ByteBufCodecs.SHORT, MultiFluidValue.STREAM_CODEC)),
                    DumbFluidUpdatePacket::updates,
                    DumbFluidUpdatePacket::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
