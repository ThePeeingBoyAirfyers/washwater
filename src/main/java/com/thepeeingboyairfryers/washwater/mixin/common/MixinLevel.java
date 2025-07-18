package com.thepeeingboyairfryers.washwater.mixin.common;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Level.class)
public class MixinLevel {

    /*
    //TODO there is now something called a neigborupdater
    private BlockState neighborChanged(Level instance, BlockPos blockPos, boolean isMoving, Operation<BlockState> original) {
        var result = original.call(instance, blockPos);
        FluidTicker.tickIfWater((ServerLevel) instance, result, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return result;
    }
    */
}
