package com.thepeeingboyairfryers.washwater.common.scheduling;

import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class FluidScheduler {
    public static final FluidScheduler INSTANCE = new FluidScheduler();

    private final ConcurrentLinkedQueue<QueuedSection> queue = new ConcurrentLinkedQueue<>();


    public void schedule(LevelChunk chunk, int sectionIndex, ShortList tick) {
        queue.add(new QueuedSection(
                chunk.getSection(sectionIndex),
                tick,
                chunk.getPos().x << 4,
                chunk.getSectionYFromSectionIndex(sectionIndex) << 4,
                chunk.getPos().z << 4
        ));
    }

    public void execute(Consumer<Runnable> queueTask) {
        for (QueuedSection section : this.queue) {
            queueTask.accept(tickSection(section));
        }
    }

    private Runnable tickSection(QueuedSection section) {
        return () -> {

        };
    }

    private record QueuedSection(LevelChunkSection section, ShortList tick, int x, int y, int z) {
    }
}
