package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.duck.IFluidState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(FluidState.class)
public abstract class MixinFluidState implements IFluidState {
    @Shadow
    public abstract Fluid getType();

    @Shadow
    public abstract boolean isEmpty();

    @Override
    public @NotNull MultiFluidValue ww€getFluid() {
        if (isEmpty()) return MultiFluidValue.EMPTY;
        return MultiFluidValue.single(getType().getFluidType(), (short) 1000);
    }
}
