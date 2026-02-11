package com.thepeeingboyairfryers.washwater.common.fluids;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.IntSupplier;
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

    Codec<MultiFluidValue> CODEC = Codec.INT_STREAM.xmap(
            stream -> {
                var array = stream.toArray();
                return deserialize(new IntSupplier() {
                    private int i = 0;

                    @Override
                    public int getAsInt() {
                        return array[i++];
                    }
                }, (int) array.length);
            },
            f -> IntStream.of(serialize(f)));

    StreamCodec<ByteBuf, MultiFluidValue> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(ByteBuf buffer, @NotNull MultiFluidValue value) {
            int[] array = serialize(value);
            buffer.writeInt(array.length);
            for (int i : array) {
                buffer.writeInt(i);
            }
        }

        @Override
        public @NotNull MultiFluidValue decode(ByteBuf buffer) {
            int size = buffer.readInt();
            return deserialize(buffer::readInt, size);
        }
    };

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
        if (volume == 0) return EMPTY;
        return new SingleFluidValue(fluidType, volume);
    }

    private static int[] serialize(MultiFluidValue f) {
        if (f.isEmpty()) return new int[] {};
        if (f instanceof SingleFluidValue s) {
            int i = FluidManager.getFluidId(s.getFluidType()) & 0xFFFF;
            i |= (s.getVolume() << 16);

            return new int[] {i};
        }

        throw new UnsupportedOperationException("Multi fluid values with more than one fluid are not supported yet");
    }

    private static MultiFluidValue deserialize(IntSupplier supplier, int size) {
        if (size == 0) return EMPTY;


        if (size == 1) {
            int i = supplier.getAsInt();
            return single(FluidManager.getFluidType((short) (i & 0xFFFF)), (short) ((i >>> 16)));
        }

        throw new UnsupportedOperationException("Multi fluid values with more than one fluid are not supported yet");
    }
}
