package com.thepeeingboyairfryers.washwater.common.packets;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.util.WWStreamCodecs;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public record SingleFuidUpdatePacket(
        SectionPos pos,
        FluidType fluidType,
        int[] positionValues
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SingleFuidUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(WashWater.resource("single_fluid_update"));
    public static final StreamCodec<FriendlyByteBuf, SingleFuidUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    WWStreamCodecs.SECTION_POS,
                    SingleFuidUpdatePacket::pos,
                    FluidManager.FLUID_STREAM_CODEC,
                    SingleFuidUpdatePacket::fluidType,
                    WWStreamCodecs.INT_ARRAY,
                    SingleFuidUpdatePacket::positionValues,
                    SingleFuidUpdatePacket::new
            );


    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
