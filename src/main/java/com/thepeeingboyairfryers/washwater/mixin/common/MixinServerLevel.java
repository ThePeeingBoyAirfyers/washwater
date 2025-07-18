package com.thepeeingboyairfryers.washwater.mixin.common;


import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        FluidTicker.tick((ServerLevel) (Object) this);
    }

    /**
     * @author SirWashington
     * @reason Vanilla fluid ticking is relieved of its duty
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {
        FluidTicker.tickWater((ServerLevel) (Object) this, pos);
    }
}

