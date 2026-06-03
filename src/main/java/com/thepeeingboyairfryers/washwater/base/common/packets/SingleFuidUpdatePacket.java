package com.thepeeingboyairfryers.washwater.base.common.packets;

import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.util.WWStreamCodecs;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class SingleFuidUpdatePacket extends SectionUpdatePacket {
    public static final CustomPacketPayload.Type<SingleFuidUpdatePacket> TYPE = newType("single_fluid_update");
    public static final StreamCodec<FriendlyByteBuf, SingleFuidUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    WWStreamCodecs.SECTION_POS,
                    SingleFuidUpdatePacket::getPos,
                    FluidManager.FLUID_STREAM_CODEC,
                    SingleFuidUpdatePacket::getFluidType,
                    WWStreamCodecs.INT_ARRAY,
                    SingleFuidUpdatePacket::getPositionValues,
                    SingleFuidUpdatePacket::new
            );

    private final SectionPos pos;
    private final FluidType fluidType;
    private final int[] positionValues;

    public SingleFuidUpdatePacket(SectionPos iPos, FluidType iFluidType, int[] iPositionValues) {
        this.pos = iPos;
        this.fluidType = iFluidType;
        this.positionValues = iPositionValues;
    }

    @Override
    public SectionPos getPos() {
        return pos;
    }

    public FluidType getFluidType() {
        return fluidType;
    }

    public int[] getPositionValues() {
        return positionValues;
    }

    @Override
    public void handle(FluidSection section) {
        for (var u : positionValues) {
            short p = (short) (u >>> 16);
            short volume = (short) (u & 0xFFFF);
            var x = FluidSection.short2localX(p);
            var y = FluidSection.short2localY(p);
            var z = FluidSection.short2localZ(p);
            section.setVolume(x, y, z, MultiFluidValue.single(fluidType, volume));
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
