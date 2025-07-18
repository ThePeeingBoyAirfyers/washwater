package com.thepeeingboyairfryers.washwater.client;

public class ClientNetworking {
/*
    public static void register() {
        LasagnaNetworking.packetClient(DeltaFluidSectionPacket.class, false, (packet, context) -> {
            var chunk = context.level.getChunk(
                    packet.chunkX,
                    packet.chunkZ
            );
            var section = chunk.getSections()[packet.sectionY];

            FluidSection storage = (FluidSection) ((ExtraStorageSectionContainer) section).getSectionStorage(FluidSection.ID);

            if (storage == null) {
                storage = new FluidSection(chunk, packet.sectionY);
                ((ExtraStorageSectionContainer) section).setSectionStorage(FluidSection.ID, storage);
            }

            boolean[] shouldUpdate = new boolean[6];

            storage.applyDelta(packet, shouldUpdate);

            ((LevelRendererAccessor) context.levelRenderer).invokeSetSectionDirty(
                    packet.chunkX,
                    context.level.getSectionYFromSectionIndex(packet.sectionY),
                    packet.chunkZ,
                    true
            );

            for (int i = 0; i < shouldUpdate.length; i++) {
                if (shouldUpdate[i]) {
                    Direction direction = Direction.values()[i];

                    ((LevelRendererAccessor) context.levelRenderer).invokeSetSectionDirty(
                            packet.chunkX + direction.getStepX(),
                            context.level.getSectionYFromSectionIndex(packet.sectionY) + direction.getStepY(),
                            packet.chunkZ + direction.getStepZ(),
                            true
                    );
                }
            }

            return Unit.INSTANCE;
        });
    }
    */


}
