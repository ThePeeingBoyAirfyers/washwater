package com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.basic;

import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.WWFluidRenderer;
import com.thepeeingboyairfryers.washwater.base.client.fluid_rendering.impl.FluidRenderingState;
import net.minecraft.core.Direction;

public class BasicFluidRenderer implements WWFluidRenderer {
    private final FluidRenderingState state;
    private final FluidFaceHandler handler;

    public BasicFluidRenderer(FluidRenderingState iState) {
        this.state = iState;
        this.handler = new FluidFaceHandler(iState);
    }

    @Override
    public void render() {
        state.getQuad().setSprite(state.getSprites()[0]);
        handler.fetchHeights();
        for (Direction dir : Direction.values()) {
            if (handler.configureFace(dir)) {
                state.writeQuad(dir);
            }
        }
    }
}
