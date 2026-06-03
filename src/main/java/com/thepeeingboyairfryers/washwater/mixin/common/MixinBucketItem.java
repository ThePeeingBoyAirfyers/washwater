package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Final;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BucketItem.class)
public abstract class MixinBucketItem {

    @Shadow
    @Final
    private Fluid content;

    @Redirect(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"

            )
    )
    private boolean ww€setBlock(Level level, BlockPos pos, BlockState state, int flags) {
        if (pos.getY() == WaterInfo.MIN_Y)
            return false;
        if (!level.isClientSide) {
            return (FluidUtil.addVolume(level, pos, content.getFluidType(), WaterInfo.VOLUME_PER_BLOCK) == WaterInfo.VOLUME_PER_BLOCK);
        } else {
            return (FluidUtil.canAddVolume(level, pos, content.getFluidType(), WaterInfo.VOLUME_PER_BLOCK) == WaterInfo.VOLUME_PER_BLOCK);
        }
    }

    @Redirect(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"

            )
    )
    private boolean isSource(FluidState instance) {
        return false;
    }
}
