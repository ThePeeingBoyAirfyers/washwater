package com.thepeeingboyairfryers.washwater.common.fluids;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.stream.IntStream;

public interface MultiFluidValue extends Iterable<MultiFluidValue.Entry> {
    MultiFluidValue EMPTY = new MultiFluidValue() {
        @Override
        public @NotNull Iterator<Entry> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public short forFluid(@NotNull FluidType type) {
            return 0;
        }

        @Override
        public short getTotalVolume() {
            return 0;
        }

        @Override
        public @NotNull MultiFluidValue setFluid(FluidType type, short value) {
            if (value == 0) return this;
            return single(type, value);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };

    Codec<MultiFluidValue> CODEC = Codec.INT_STREAM.xmap(i -> {
        var a = i.toArray();
        if (a.length == 0) return EMPTY;

        if (a.length == 1)
            return single(FluidManager.getFluidType((short) (a[0] & 0xFFFF)), (short) (a[0] >>> 16));

        throw new UnsupportedOperationException("Multi fluid values with more than one fluid are not supported yet");
    }, f -> {
        if (f.isEmpty()) return IntStream.empty();
        if (f instanceof SingleFluidValue s) {
            int i = FluidManager.getFluidId(s.getFluidType());
            i |= (s.getVolume() << 16);

            return IntStream.of(i);
        }

        throw new UnsupportedOperationException("Multi fluid values with more than one fluid are not supported yet");
    });

    StreamCodec<ByteBuf, MultiFluidValue> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);

    boolean isEmpty();
    int size();

    short forFluid(@NotNull FluidType type);

    default short getTotalVolume() {
        short result = 0;
        for (Entry e : this) {
            result += e.volume();
        }
        return result;
    }

    @NotNull MultiFluidValue setFluid(FluidType type, short value);

    record Entry(short volume, FluidType fluidType) { }

    static @NotNull MultiFluidValue single(@NotNull FluidType fluidType, short volume) {
        return new SingleFluidValue(fluidType, volume);
    }
}
