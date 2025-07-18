package com.thepeeingboyairfryers.washwater.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Level.class)
public class MixinLevel {

    @WrapOperation(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
    private BlockState neighborChanged(Level instance, BlockPos blockPos, Operation<BlockState> original) {
        var result = original.call(instance, blockPos);
        FluidTicker.tickIfWater((ServerLevel) instance, result, blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return result;
    }

}
