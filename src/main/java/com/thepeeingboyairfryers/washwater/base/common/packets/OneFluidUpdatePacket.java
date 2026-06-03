package com.thepeeingboyairfryers.washwater.base.common.packets;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record OneFluidUpdatePacket(
        BlockPos pos,
        MultiFluidValue value
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<OneFluidUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(WashWater.resource("one_fluid_update"));
    public static final StreamCodec<ByteBuf, OneFluidUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC,
                    OneFluidUpdatePacket::pos,
                    MultiFluidValue.STREAM_CODEC,
                    OneFluidUpdatePacket::value,
                    OneFluidUpdatePacket::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
