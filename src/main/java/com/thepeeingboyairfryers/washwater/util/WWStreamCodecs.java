package com.thepeeingboyairfryers.washwater.util;

import com.mojang.datafixers.util.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class WWStreamCodecs {
    private WWStreamCodecs() {
        throw new IllegalStateException("Utility class");
    }

    public static final StreamCodec<ByteBuf, SectionPos> SECTION_POS = ByteBufCodecs.VAR_LONG.map(SectionPos::of, SectionPos::asLong);
    public static final StreamCodec<FriendlyByteBuf, int[]> INT_ARRAY = new StreamCodec<>() {
        @Override
        public int @NotNull [] decode(FriendlyByteBuf buffer) {
            int[] array = new int[buffer.readVarInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = buffer.readInt();
            }

            return array;
        }

        @Override
        public void encode(FriendlyByteBuf buffer, int[] value) {
            buffer.writeVarInt(value.length);
            for (int j : value) {
                buffer.writeInt(j);
            }
        }
    };

    public static final StreamCodec<FriendlyByteBuf, short[]> SHORT_ARRAY = new StreamCodec<>() {
        @Override
        public short @NotNull [] decode(FriendlyByteBuf buffer) {
            short[] array = new short[buffer.readVarInt()];
            for (int i = 0; i < array.length; i++) {
                array[i] = buffer.readShort();
            }

            return array;
        }

        @Override
        public void encode(FriendlyByteBuf buffer, short[] value) {
            buffer.writeVarInt(value.length);
            for (short j : value) {
                buffer.writeShort(j);
            }
        }
    };

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
