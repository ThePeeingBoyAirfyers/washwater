package com.thepeeingboyairfryers.washwater.util.poker;

import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionList;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.SimpleFluidSectionList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

public class FluidSectionPoker {
    private static boolean enabled = false;

    private FluidSectionPoker() {
        throw new AssertionError();
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void isEnabled(boolean isEnabled) {
        enabled = isEnabled;
    }


    public static FluidSectionList createList(ChunkAccess chunk) {
        if (!isEnabled() || chunk.getLevel() == null || !chunk.getLevel().isClientSide())
            return new SimpleFluidSectionList(chunk.getSectionsCount());

        return new PokedFluidSectionList(getServerSideList(chunk));
    }

    public static FluidSectionList configureList(ChunkAccess chunk, FluidSectionList sections) {
        if (!isEnabled() || chunk.getLevel() == null) return sections;
        if (!chunk.getLevel().isClientSide()) return sections;
        if (sections instanceof PokedFluidSectionList) return sections;

        return new PokedFluidSectionList(getServerSideList(chunk));
    }

    private static FluidSectionList getServerSideList(ChunkAccess chunk) {
        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server == null) throw new IllegalStateException("Cant do poking on a non singleplayer server");
        ServerLevel associatedLevel = server.getLevel(chunk.getLevel().dimension());
        if (associatedLevel == null) throw new IllegalStateException("Poking didnt find same level?");
        LevelChunk sChunk = associatedLevel.getChunk(chunk.getPos().x, chunk.getPos().z);
        return FluidSectionManager.getAttachmentFor(sChunk).getSectionList();
    }


    public static void markDirty(Level level, int x, int y, int z) {
        if (!isEnabled()) return;
        Minecraft.getInstance().submit(() ->
                Minecraft.getInstance().levelRenderer.setSectionDirty(x, y, z)
        );
    }
}
