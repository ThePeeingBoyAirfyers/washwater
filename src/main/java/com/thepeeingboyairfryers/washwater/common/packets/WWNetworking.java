package com.thepeeingboyairfryers.washwater.common.packets;

import com.thepeeingboyairfryers.washwater.common.Config;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
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
            r.playToClient(DumbFluidUpdatePacket.TYPE, DumbFluidUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunk(p.pos().x(), p.pos().z());
                var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(p.pos().y());
                for (var u : p.updates()) {
                    var x = FluidSection.short2localX(u.getFirst());
                    var y = FluidSection.short2localY(u.getFirst());
                    var z = FluidSection.short2localZ(u.getFirst());
                    section.setVolume(x, y, z, u.getSecond());
                }
            });

            r.playToClient(OneFluidUpdatePacket.TYPE,  OneFluidUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunkAt(p.pos());
                FluidSectionManager.getAttachmentFor(chunk).setVolume(p.pos().getX(), p.pos().getY(), p.pos().getZ(), p.value());
            });

            r.playToClient(SingleFuidUpdatePacket.TYPE, SingleFuidUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunk(p.pos().x(), p.pos().z());
                var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(p.pos().y());
                for (var u : p.positionValues()) {
                    short pos = (short) (u >>> 16);
                    short volume = (short) (u & 0xFFFF);
                    var x = FluidSection.short2localX(pos);
                    var y = FluidSection.short2localY(pos);
                    var z = FluidSection.short2localZ(pos);
                    section.setVolume(x, y, z, MultiFluidValue.single(p.fluidType(), volume));
                }
            });
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
