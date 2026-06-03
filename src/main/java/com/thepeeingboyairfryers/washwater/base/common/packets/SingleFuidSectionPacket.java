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

public class SingleFuidSectionPacket extends SectionUpdatePacket {
    public static final Type<SingleFuidSectionPacket> TYPE = newType("single_fluid_section_update");
    public static final StreamCodec<FriendlyByteBuf, SingleFuidSectionPacket> STREAM_CODEC =
            StreamCodec.composite(
                    WWStreamCodecs.SECTION_POS,
                    SingleFuidSectionPacket::getPos,
                    FluidManager.FLUID_STREAM_CODEC,
                    SingleFuidSectionPacket::getFluidType,
                    WWStreamCodecs.SHORT_ARRAY,
                    SingleFuidSectionPacket::getVolumes,
                    SingleFuidSectionPacket::new
            );

    private final SectionPos pos;
    private final FluidType fluidType;
    private final short[] volumes;

    public SingleFuidSectionPacket(SectionPos iPos, FluidType iFluidType, short[] iVolumes) {
        this.pos = iPos;
        this.fluidType = iFluidType;
        this.volumes = iVolumes;
    }

    @Override
    public SectionPos getPos() {
        return pos;
    }

    public FluidType getFluidType() {
        return fluidType;
    }

    public short[] getVolumes() {
        return volumes;
    }

    @Override
    public void handle(FluidSection section) {
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    short volume = volumes[FluidSection.localPos2Short(x, y, z)];
                    section.setVolume(x, y, z, MultiFluidValue.single(fluidType, volume));
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
