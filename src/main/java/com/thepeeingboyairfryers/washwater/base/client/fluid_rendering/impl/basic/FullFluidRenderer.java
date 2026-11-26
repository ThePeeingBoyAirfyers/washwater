package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.basic;

import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRenderer;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import net.minecraft.core.Direction;

public class FullFluidRenderer implements WWFluidRenderer {
    private final FluidRenderingState state;
    private final FluidFaceHandler handler;

    public FullFluidRenderer(FluidRenderingState iState) {
        this.state = iState;
        this.handler = new FluidFaceHandler(iState);
    }

    @Override
    public void render() {
        state.getQuad().setSprite(state.getSprites()[0]);
        handler.setCornerHeights(1f, 1f, 1f, 1f);
        for (Direction dir : Direction.values()) {
            if (handler.configureFace(dir)) {
                state.writeQuad(dir);
            }
        }
    }
}
