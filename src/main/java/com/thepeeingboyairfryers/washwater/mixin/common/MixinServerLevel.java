package com.thepeeingboyairfryers.washwater.mixin.common;


import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    /**
     * @author SirWashington
     * @param pos The position of the fluid to tick
     * @param fluid The fluid to tick
     * @reason Vanilla fluid ticking is relieved of its duty
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {
        FluidTicker.tickWater((ServerLevel) (Object) this, pos);
    }
}

