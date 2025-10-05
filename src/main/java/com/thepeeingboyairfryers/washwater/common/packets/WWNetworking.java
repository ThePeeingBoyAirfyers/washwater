package com.thepeeingboyairfryers.washwater.common.packets;

import com.thepeeingboyairfryers.washwater.common.storage.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class WWNetworking {
    private WWNetworking() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(IEventBus bus) {
        bus.addListener((RegisterPayloadHandlersEvent e) -> {
            var r = e.registrar("1");
            r.playToClient(DumbFluidSectionUpdatePacket.TYPE, DumbFluidSectionUpdatePacket.STREAM_CODEC, (p, ctx) -> {
                var chunk = Minecraft.getInstance().level.getChunk(p.pos().x(), p.pos().z());
                var section = FluidSectionManager.getAttachmentFor(chunk).getSectionWithY(p.pos().y());
                for (var u : p.updates()) {
                    var x = FluidSection.short2localX(u.getFirst());
                    var y = FluidSection.short2localY(u.getFirst());
                    var z = FluidSection.short2localZ(u.getFirst());
                    System.out.println("Recieved update at " + x + ", " + y + ", " + z);
                    synchronized (section) {
                        section.setVolume(x, y, z, u.getSecond());
                    }
                }
            });
        });

        NeoForge.EVENT_BUS.addListener((LevelTickEvent.Post e) -> {
            var l = e.getLevel();
            if (!l.isClientSide) {
                int idx = 0;
                for (var s : FluidSectionManager.getAttachmentFor(l.getChunk(0, 0))) {
                    synchronized (s) {
                        if (s instanceof DumbFluidSection ds) {
                            var p = ds.buildUpdate(SectionPos.of(0, l.getMinSection() + idx++, 0), false);
                            if (p.updates().isEmpty()) continue;
                            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) l, new ChunkPos(0, 0), p);
                        }
                    }
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
                    if (s instanceof DumbFluidSection ds) {
                        var p = ds.buildUpdate(SectionPos.of(cPos.x, l.getMinSection() + idx++, cPos.z), true);
                        if (p.updates().isEmpty()) continue;
                        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) l, new ChunkPos(0, 0), p);
                    }
                }
            }
        });
    }
}
