package com.thepeeingboyairfryers.washwater.common.scheduling;

import com.thepeeingboyairfryers.washwater.mixin.common.ChunkMapAccessor;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;

public class ActiveChunks {
    private ActiveChunks() {
        throw new IllegalStateException();
    }

    public static void getActiveChunks(ServerLevel level, LongSet current) {
        ServerChunkCache chunkSource = level.getChunkSource();
        var loadedChunksList = ((ChunkMapAccessor) chunkSource.chunkMap).getAllChunks();
        for (var chunkHolder : loadedChunksList) {
            chunkHolder.getTickingChunkFuture()
                    .getNow(ChunkHolder.UNLOADED_LEVEL_CHUNK)
                    .ifSuccess(chunk -> current.add(chunk.getPos().toLong()));
        }
    }
}
