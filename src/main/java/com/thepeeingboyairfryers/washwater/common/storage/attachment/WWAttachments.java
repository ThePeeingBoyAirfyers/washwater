package com.thepeeingboyairfryers.washwater.common.storage.attachment;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class WWAttachments {
    private WWAttachments() {
        throw new IllegalStateException();
    }

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, WashWater.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FluidChunkAttachment>> FLUID_CHUNK =
            ATTACHMENTS.register(
                    "fluid",
                    () -> AttachmentType
                            .builder(FluidChunkAttachment::new)
                            .serialize(FluidChunkAttachment.CODEC.codec())
                            .build()
            );

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<FluidsIndexationAttachment>> FLUIDS_INDEXATION =
            ATTACHMENTS.register(
                    "fluids_indexation",
                    () -> AttachmentType
                            .builder(FluidsIndexationAttachment::create)
                            .serialize(FluidsIndexationAttachment.CODEC.codec())
                            .build()
            );

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
