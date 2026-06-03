package com.thepeeingboyairfryers.washwater.mixin.common;


import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ServerLevel.class)
public abstract class MixinServerLevel {

    /**
     * @param pos   The position of the fluid to tick
     * @param fluid The fluid to tick
     * @author SirWashington
     * @reason Vanilla fluid ticking is relieved of its duty
     */
    @Overwrite
    private void tickFluid(BlockPos pos, Fluid fluid) {
        FluidTicker.tickIfFluid((ServerLevel) (Object) this, pos.getX(), pos.getY(), pos.getZ());
    }
}

