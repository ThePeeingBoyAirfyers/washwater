package com.thepeeingboyairfryers.washwater.base.common.storage.attachment;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WWAttachments {
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
                            .sync(new AttachmentSyncHandler<>() {
                                private final StreamCodec<ByteBuf, FluidsIndexationAttachment> codec = ByteBufCodecs.fromCodec(FluidsIndexationAttachment.CODEC.codec());

                                @Override
                                public void write(@NotNull RegistryFriendlyByteBuf buf, @NotNull FluidsIndexationAttachment attachment, boolean initialSync) {
                                    codec.encode(buf, attachment);
                                }

                                @Override
                                public @NotNull FluidsIndexationAttachment read(@NotNull IAttachmentHolder holder, @NotNull RegistryFriendlyByteBuf buf, @Nullable FluidsIndexationAttachment previousValue) {
                                    var result = codec.decode(buf);
                                    FluidManager.setFluidsIndexation(result);
                                    return result;
                                }
                            })
                            .build()
            );

    private WWAttachments() {
        throw new IllegalStateException();
    }

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}
