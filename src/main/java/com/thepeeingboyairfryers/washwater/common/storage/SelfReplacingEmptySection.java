package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SelfReplacingEmptySection extends UpgradeableFluidSection {

    @Override
    protected void volume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        upgrade(new DumbFluidSection());
        setVolume(x, y, z, fluids);
    }

    @Override
    protected @NotNull MultiFluidValue volume(int x, int y, int z) {
        return MultiFluidValue.EMPTY;
    }

    @Override
    protected boolean empty() {
        return true;
    }

    @Override
    protected @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        return null;
    }

    @Override
    protected @NotNull MapCodec<? extends FluidSection> myCodec() {
        return FluidSection.EMPTY.codec();
    }
}
