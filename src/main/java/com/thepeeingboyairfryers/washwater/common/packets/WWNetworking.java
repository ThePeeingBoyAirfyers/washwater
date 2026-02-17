package com.thepeeingboyairfryers.washwater.common.packets;

import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
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

public class WWNetworking {
    private WWNetworking() {
        throw new IllegalStateException("Utility class");
    }
    private static final Map<ServerLevel, LongSet> DIRTY_SECTIONS = new HashMap<>();

    public static void register(IEventBus bus) {
        bus.addListener((RegisterPayloadHandlersEvent e) -> {
            var r = e.registrar("1");
            r.playToClient(DumbFluidSectionUpdatePacket.TYPE, DumbFluidSectionUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunk(p.pos().x(), p.pos().z());
                var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(p.pos().y());
                synchronized (section) {
                    for (var u : p.updates()) {
                        var x = FluidSection.short2localX(u.getFirst());
                        var y = FluidSection.short2localY(u.getFirst());
                        var z = FluidSection.short2localZ(u.getFirst());
                        section.setVolume(x, y, z, u.getSecond());
                    }
                }
            });
        });

        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post e) -> {
            if (e.getLevel().isClientSide()) return;
            var l = e.getLevel();
            var dirties = DIRTY_SECTIONS.get(l);
            if (dirties == null) return;
            for (var lPos : dirties) {
                var sectionPos = SectionPos.of(lPos);
                var chunkPos = sectionPos.chunk();
                var chunk = l.getChunk(chunkPos.x, chunkPos.z);
                var s = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(sectionPos.y());
                synchronized (s) {
                    var update = s.updatePacket(sectionPos, false);
                    if (update == null) continue;
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) l, chunkPos, update);
                }
            }
        });

        NeoForge.EVENT_BUS.addListener((ChunkWatchEvent.Sent e) -> {
            var l = e.getLevel();
            var cPos = e.getPos();
            var attachment = FluidSectionManager.getAttachmentFor(l.getChunk(cPos.x, cPos.z));
            int idx = 0;
            for (var s : attachment) {
                synchronized (s) {
                    var update = s.updatePacket(SectionPos.of(cPos.x, l.getMinSection() + idx++, cPos.z), true);
                    if (update == null) continue;
                    PacketDistributor.sendToPlayer(e.getPlayer(), update);
                }
            }
        });
    }

    //SectionPos coordinates
    public static void queueUpdate(ServerLevel level, int x, int y, int z) {
        DIRTY_SECTIONS.computeIfAbsent(level, a -> new LongRBTreeSet()).add(SectionPos.asLong(x, y, z));
    }
}
