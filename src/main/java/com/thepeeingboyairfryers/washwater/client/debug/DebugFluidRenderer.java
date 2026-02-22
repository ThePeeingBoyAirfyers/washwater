package com.thepeeingboyairfryers.washwater.client.debug;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;

public class DebugFluidRenderer {
    private DebugFluidRenderer() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(IEventBus bus) {
        if (System.getProperty("ww.debug") == null) return;
        NeoForge.EVENT_BUS.addListener(DebugFluidRenderer::render);
    }

    private static void render(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource source = Minecraft.getInstance().renderBuffers().bufferSource();

        var attachment = FluidSectionManager.getAttachmentFor(Minecraft.getInstance().level.getChunk(0, 0));
        int y = Minecraft.getInstance().level.getMinBuildHeight();
        for (var s : attachment) {
            if (s instanceof DumbFluidSection d) {
                int finalY = y;
                d.allKeys().forEach(e ->
                        renderFluid(poseStack, source, e.getX(), e.getY() + finalY, e.getZ(), s.getVolume(e.getX(), e.getY(), e.getZ()))
                );
            }
            y += 16;
        }
    }

    private static void renderFluid(PoseStack poseStack, MultiBufferSource source, int x, int y, int z, MultiFluidValue value) {
        DebugRenderer.renderFloatingText(poseStack, source, Short.toString(value.getTotalVolume()), x, y, z, 0xFFFFFFFF);
    }

}
