package com.thepeeingboyairfryers.washwater.common.storage;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.packets.SingleFuidUpdatePacket;
import com.thepeeingboyairfryers.washwater.common.util.WWCodecs;
import it.unimi.dsi.fastutil.shorts.ShortRBTreeSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SingleFluidSection extends UpgradeableFluidSection {
    public static final MapCodec<SingleFluidSection> CODEC = RecordCodecBuilder.mapCodec(b -> b.group(
            NeoForgeRegistries.FLUID_TYPES.byNameCodec().fieldOf("fluidType").forGetter(SingleFluidSection::getFluidType),
            WWCodecs.SHORT_ARRAY.fieldOf("volumes").forGetter(SingleFluidSection::getVolumes),
            Codec.INT.fieldOf("nonEmpty").forGetter(SingleFluidSection::getNonEmpty)
    ).apply(b, SingleFluidSection::new));

    private final FluidType fluidType;
    private final short[] volumes;
    private final ShortSet dirty = new ShortRBTreeSet();
    private int nonEmpty;

    public SingleFluidSection(FluidType iFluidType) {
        this(iFluidType, new short[16 * 16 * 16], 0);
    }

    public SingleFluidSection(FluidType iFluidType, short[] iVolumes, int iNonEmpty) {
        this.fluidType = iFluidType;
        this.volumes = iVolumes;
        this.nonEmpty = iNonEmpty;
    }

    @Override
    protected void volume(int x, int y, int z, @NotNull MultiFluidValue fluids) {
        if (fluids.size() <= 1) {
            short v = fluids.forFluid(fluidType);
            if (v == 0 && !fluids.isEmpty()) {
                upgrade();
                setVolume(x, y, z, fluids);
                return;
            }

            short oldValue = volumes[FluidSection.localPos2Short(x, y, z)];
            if (oldValue == v) return;

            if (oldValue == 0) {
                nonEmpty++;
            } else if (v == 0) {
                nonEmpty--;
            }

            dirty.add(FluidSection.localPos2Short(x, y, z));
            volumes[FluidSection.localPos2Short(x, y, z)] = v;
            markDirty();
        } else if (fluids.size() > 1) {
            upgrade();
            setVolume(x, y, z, fluids);
        }
    }

    @Override
    protected @NotNull MultiFluidValue volume(int x, int y, int z) {
        return MultiFluidValue.single(fluidType, volumes[FluidSection.localPos2Short(x, y, z)]);
    }

    @Override
    protected boolean empty() {
        return nonEmpty == 0;
    }

    @Override
    protected @Nullable CustomPacketPayload updatePacket(SectionPos pos, boolean all) {
        if (all) {
            // TODO
            return null;
        } else {
            if (dirty.isEmpty()) return null;
            int[] updates = new int[dirty.size()];
            int i = 0;
            for (short s : dirty) {
                updates[i++] = (s << 16) | volumes[s];
            }

            dirty.clear();
            return new SingleFuidUpdatePacket(pos, fluidType, updates);
        }
    }

    @Override
    protected @NotNull MapCodec<? extends FluidSection> myCodec() {
        return CODEC;
    }

    private void upgrade() {
        DumbFluidSection section = new DumbFluidSection();
        section.copyFrom(this);
        upgrade(section);
    }

    public FluidType getFluidType() {
        return fluidType;
    }

    public short[] getVolumes() {
        return volumes;
    }

    public int getNonEmpty() {
        return nonEmpty;
    }
}
