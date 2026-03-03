package com.thepeeingboyairfryers.washwater.common.packets;

import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class WWNetworking {
    private static final Map<ServerLevel, Long2ObjectMap<CompletableFuture<Void>>> DIRTY_SECTIONS = new HashMap<>();

    private WWNetworking() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(IEventBus bus) {
        bus.addListener((RegisterPayloadHandlersEvent e) -> {
            var r = e.registrar("1");
            r.playToClient(DumbFluidSectionUpdatePacket.TYPE, DumbFluidSectionUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunk(p.pos().x(), p.pos().z());
                var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(p.pos().y());

                section.acquireWriteLock();
                try {
                    for (var u : p.updates()) {
                        var x = FluidSection.short2localX(u.getFirst());
                        var y = FluidSection.short2localY(u.getFirst());
                        var z = FluidSection.short2localZ(u.getFirst());
                        section.setVolume(x, y, z, u.getSecond());
                    }
                } finally {
                    section.releaseWriteLock();
                }
            });
        });

        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post e) -> {
            if (e.getLevel().isClientSide()) return;
            var l = e.getLevel();
            synchronized (DIRTY_SECTIONS) {
                var dirties = DIRTY_SECTIONS.get(l);
                if (dirties == null) return;
                for (var entries : dirties.long2ObjectEntrySet()) {
                    var sectionPos = SectionPos.of(entries.getLongKey());
                    var chunkPos = sectionPos.chunk();
                    var chunk = l.getChunk(chunkPos.x, chunkPos.z);
                    var s = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(sectionPos.y());

                    //Write cus we cleanup dirties
                    s.acquireWriteLock();
                    try {
                        var update = s.buildUpdatePacket(sectionPos, false);
                        if (update != null)
                            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) l, chunkPos, update);
                    } finally {
                        s.releaseWriteLock();
                    }

                    entries.getValue().complete(null);
                }
            }
        });

        NeoForge.EVENT_BUS.addListener((ChunkWatchEvent.Sent e) -> {
            var l = e.getLevel();
            var cPos = e.getPos();
            var attachment = FluidSectionManager.getAttachmentFor(l.getChunk(cPos.x, cPos.z));
            int idx = 0;
            for (var s : attachment) {
                s.acquireReadLock(); //Read cus we just send state of water to new player

                try {
                    var update = s.buildUpdatePacket(SectionPos.of(cPos.x, l.getMinSection() + idx++, cPos.z), true);
                    if (update != null)
                        PacketDistributor.sendToPlayer(e.getPlayer(), update);
                } finally {
                    s.releaseReadLock();
                }
            }
        });
    }

    //SectionPos coordinates
    public static CompletableFuture<Void> queueUpdate(ServerLevel level, int x, int y, int z) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        synchronized (DIRTY_SECTIONS) {
            DIRTY_SECTIONS.computeIfAbsent(level, a -> new Long2ObjectAVLTreeMap<>()).put(SectionPos.asLong(x, y, z), future);
        }

        return future;
    }
}
