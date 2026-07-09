package com.thepeeingboyairfryers.washwater.util.parallel;

import com.thepeeingboyairfryers.washwater.mixin.accessors.ChunkBuilderAccessor;
import com.thepeeingboyairfryers.washwater.mixin.accessors.SodiumWorldRendererAccessor;
import net.caffeinemc.mods.sodium.client.render.SodiumWorldRenderer;

import java.util.List;

public class MainThreads {
    private static long serverThread = -1;
    private static long renderThread = -1;
    private static List<Thread> chunkBuildThreads = null;

    private MainThreads() {
        throw new AssertionError();
    }

    public static boolean isMainThread() {
        return isRenderThread() || isServerThread();
    }

    public static boolean isServerThread() { //TODO when opening another world it creates a new thread...
        if (serverThread == -1) {
            if (Thread.currentThread().getName().equals("Server thread")) {
                serverThread = Thread.currentThread().threadId();
                return true;
            } else return false;
        } else return Thread.currentThread().threadId() == serverThread;
    }

    public static boolean isRenderThread() {
        if (renderThread == -1) {
            if (Thread.currentThread().getName().equals("Render thread")) {
                renderThread = Thread.currentThread().threadId();
                return true;
            } else return false;
        } else return Thread.currentThread().threadId() == renderThread;
    }

    public static boolean isChunkBuilderThread() {
        if (chunkBuildThreads == null) {
            var renderer = (SodiumWorldRendererAccessor) SodiumWorldRenderer.instanceNullable();
            if (renderer == null) return false;

            var manager = renderer.getRenderSectionManager();
            if (manager == null) return false;

            var builder = (ChunkBuilderAccessor) manager.getBuilder();
            if (builder == null) return false;

            chunkBuildThreads = builder.getThreads();
        }

        return chunkBuildThreads.contains(Thread.currentThread());
    }
}
