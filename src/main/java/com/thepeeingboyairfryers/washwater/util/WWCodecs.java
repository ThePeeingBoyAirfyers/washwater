package com.thepeeingboyairfryers.washwater.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.LongConsumer;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

public class WWCodecs {
    public static final Codec<short[]> SHORT_ARRAY = RecordCodecBuilder.create(b -> b.group(
            Codec.INT.fieldOf("length").forGetter(a -> a.length),
            Codec.LONG_STREAM.fieldOf("values").forGetter(WWCodecs::toLongs)
    ).apply(b, WWCodecs::fromLongs));

    private WWCodecs() {
        throw new IllegalStateException("Utility class");
    }

    private static LongStream toLongs(short[] values) {
        Spliterator.OfLong spliterator = new Spliterators.AbstractLongSpliterator(Long.MAX_VALUE,
                Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL) {
            private int i = 0;

            @Override
            public boolean tryAdvance(LongConsumer action) {
                Objects.requireNonNull(action);
                int offset = i++ * 4;
                if (offset >= values.length) return false;
                long r = (long) values[offset++] << 48;
                if (offset < values.length)
                    r |= (long) values[offset++] << 32;
                if (offset < values.length)
                    r |= (long) values[offset++] << 16;
                if (offset < values.length)
                    r |= values[offset];

                action.accept(r);
                return true;
            }
        };
        return StreamSupport.longStream(spliterator, false);
    }

    private static short[] fromLongs(int length, LongStream values) {
        short[] array = new short[length];
        var iter = values.iterator();
        int idx = 0;
        while (iter.hasNext()) {
            long l = iter.nextLong();
            if (idx >= array.length) break;
            array[idx++] = (short) (l >>> 48);
            if (idx >= array.length) break;
            array[idx++] = (short) (l >>> 32);
            if (idx >= array.length) break;
            array[idx++] = (short) (l >>> 16);
            if (idx >= array.length) break;
            array[idx++] = (short) l;
        }

        return array;
    }
}
