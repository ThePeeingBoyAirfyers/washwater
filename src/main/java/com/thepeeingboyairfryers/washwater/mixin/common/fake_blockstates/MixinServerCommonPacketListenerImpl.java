package com.thepeeingboyairfryers.washwater.mixin.common.fake_blockstates;

import com.thepeeingboyairfryers.washwater.base.common.packets.OneFluidUpdatePacket;
import com.thepeeingboyairfryers.washwater.ducks.IFluidState;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.GamePacketTypes;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public abstract class MixinServerCommonPacketListenerImpl {
    @Shadow
    public abstract void send(Packet<?> packet);

    @Inject(method = "send(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketSendListener;)V", at = @At("TAIL"))
    private void includeWater(Packet<?> packet, PacketSendListener listener, CallbackInfo ci) {
        if (packet.type() == GamePacketTypes.CLIENTBOUND_BLOCK_UPDATE) {
            var update = (ClientboundBlockUpdatePacket)  packet;
            var fluid = ((IFluidState) update.getBlockState().getFluidState()).ww€getFluid();
            if (!fluid.isEmpty())
                send(new OneFluidUpdatePacket(update.getPos(), fluid).toVanillaClientbound());
        }
    }
}
