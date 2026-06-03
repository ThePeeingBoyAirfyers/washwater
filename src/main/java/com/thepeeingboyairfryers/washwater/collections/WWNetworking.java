package com.thepeeingboyairfryers.washwater.collections;

import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.base.common.packets.DumbFluidUpdatePacket;
import com.thepeeingboyairfryers.washwater.base.common.packets.OneFluidUpdatePacket;
import com.thepeeingboyairfryers.washwater.base.common.packets.SectionUpdatePacket;
import com.thepeeingboyairfryers.washwater.base.common.packets.SingleFuidSectionPacket;
import com.thepeeingboyairfryers.washwater.base.common.packets.SingleFuidUpdatePacket;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import it.unimi.dsi.fastutil.longs.LongRBTreeSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;

import java.util.HashMap;
import java.util.Map;

public class WWNetworking {
    private static final Map<ServerLevel, LongSet> DIRTY_SECTIONS = new HashMap<>();

    private WWNetworking() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(IEventBus bus) {
        bus.addListener((RegisterPayloadHandlersEvent e) -> {
            var r = e.registrar("1").executesOn(HandlerThread.MAIN);

            r.playToClient(OneFluidUpdatePacket.TYPE,  OneFluidUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunkAt(p.pos());
                FluidSectionManager.getAttachmentFor(chunk).setVolume(p.pos().getX(), p.pos().getY(), p.pos().getZ(), p.value());
            });

            r.playToClient(DumbFluidUpdatePacket.TYPE, DumbFluidUpdatePacket.STREAM_CODEC, WWNetworking::handleSectionUpdate);
            r.playToClient(SingleFuidUpdatePacket.TYPE, SingleFuidUpdatePacket.STREAM_CODEC, WWNetworking::handleSectionUpdate);
            r.playToClient(SingleFuidSectionPacket.TYPE, SingleFuidSectionPacket.STREAM_CODEC, WWNetworking::handleSectionUpdate);
        });

        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post e) -> {
            if (e.getLevel().isClientSide()) return;
            var level = e.getLevel();
            if (!shouldSendPackets(level)) return;

            var dirties = DIRTY_SECTIONS.get(level);
            if (dirties == null) return;
            for (var lPos : dirties) {
                var sectionPos = SectionPos.of(lPos);
                var chunkPos = sectionPos.chunk();
                var chunk = level.getChunk(chunkPos.x, chunkPos.z);
                var s = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(sectionPos.y());


                var update = s.buildUpdatePacket(sectionPos, false);
                if (update != null)
                    PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, chunkPos, update);
            }
        });

        NeoForge.EVENT_BUS.addListener((ChunkWatchEvent.Sent e) -> {
            var l = e.getLevel();
            var cPos = e.getPos();
            var attachment = FluidSectionManager.getAttachmentFor(l.getChunk(cPos.x, cPos.z));
            int idx = 0;
            for (var s : attachment) {
                var update = s.buildUpdatePacket(SectionPos.of(cPos.x, l.getMinSection() + idx++, cPos.z), true);
                if (update != null)
                    PacketDistributor.sendToPlayer(e.getPlayer(), update);
            }
        });
    }

    private static void handleSectionUpdate(SectionUpdatePacket packet, IPayloadContext ctx) {
        var pos = packet.getPos();
        var chunk = Minecraft.getInstance().level.getChunk(pos.x(), pos.z());
        var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(pos.y());
        packet.handle(section);
    }

    private static boolean shouldSendPackets(Level level) {
        return level.getDayTime() % Config.SEND_UPDATE_PACKETS_EVERY.getAsInt() == 0;
    }

    //SectionPos coordinates
    public static void queueUpdate(ServerLevel level, int x, int y, int z) {
        synchronized (DIRTY_SECTIONS) {
            DIRTY_SECTIONS.computeIfAbsent(level, a -> new LongRBTreeSet()).add(SectionPos.asLong(x, y, z));
        }
    }
}
