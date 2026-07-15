package com.thepeeingboyairfryers.washwater.debug;


import com.mojang.blaze3d.vertex.PoseStack;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.base.common.storage.impl.sections.DumbFluidSection;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
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
        //int y = Minecraft.getInstance().level.getMinBuildHeight();
        //FluidUtil.getVolume(Minecraft.getInstance().level, x, y, z, FluidUtil.WATER_TYPE )

        for (int x = 0; x < 16; x++){
            for (int z = 0; z < 16; z++) {
                for (int y = -63; y < 0; y++) {
                    if (FluidUtil.getFluids(Minecraft.getInstance().level, new BlockPos(x,y,z)).getTotalVolume() > 0)
                        renderFluid(poseStack, source, x, y, z, FluidUtil.getFluids(Minecraft.getInstance().level, new BlockPos(x,y,z)));

                }
            }
        }


    }

    private static void renderFluid(PoseStack poseStack, MultiBufferSource source, int x, int y, int z, MultiFluidValue value) {
        DebugRenderer.renderFloatingText(poseStack, source, Short.toString(value.getTotalVolume()), x, y, z, 0xFFFFFFFF);
    }
}
