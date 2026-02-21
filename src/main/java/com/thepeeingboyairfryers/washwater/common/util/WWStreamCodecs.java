package com.thepeeingboyairfryers.washwater.common.util;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class WWStreamCodecs {
    public static final StreamCodec<ByteBuf, SectionPos> SECTION_POS = ByteBufCodecs.VAR_LONG.map(SectionPos::of, SectionPos::asLong);

    private WWStreamCodecs() {
        throw new IllegalStateException("Utility class");
    }

    public static <B, T1, T2> StreamCodec<B, Pair<T1, T2>> pair(StreamCodec<? super B, T1> c1, StreamCodec<? super B, T2> c2) {
        return StreamCodec.composite(
                c1,
                Pair::getFirst,
                c2,
                Pair::getSecond,
                Pair::new
        );
    }
}
