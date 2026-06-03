package com.thepeeingboyairfryers.washwater.base.common.packets;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public abstract class SectionUpdatePacket implements CustomPacketPayload {
    protected static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> newType(String name) {
        return new CustomPacketPayload.Type<>(WashWater.resource(name));
    }

    public abstract SectionPos getPos();

    public abstract void handle(FluidSection section);
}
