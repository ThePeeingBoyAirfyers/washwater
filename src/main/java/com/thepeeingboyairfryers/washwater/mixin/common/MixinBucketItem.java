package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(BucketItem.class)
public abstract class MixinBucketItem {





/*    @Inject(
            at = @At(value = "INVOKE", target = ""), method = "emptyContents", cancellable = true
    )*/


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
            if (FluidUtil.addVolume((ServerLevel) level, pos, WaterInfo.WATER_TYPE, WaterInfo.VOLUME_PER_BLOCK, true)) {
                WashWater.LOGGER.warn("mixin true");
                return true;
            }
            else {
                WashWater.LOGGER.warn("mixin false 1");
                return false;
            }

        } else {
            if (FluidUtil.addVolume(level, pos, WaterInfo.WATER_TYPE, WaterInfo.VOLUME_PER_BLOCK, false)) {
                WashWater.LOGGER.warn("mixin true client");
                return true;
            }
            else {
                System.out.println("mixin false client");
                return false;
            }
        }
    }

/*    @Inject(at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/material/FluidState;isSource()Z"),
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            cancellable = true)

    private void isSource(Player player, Level level, BlockPos pos, BlockHitResult result, ItemStack container, CallbackInfoReturnable<Boolean> cir) {

    }*/

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
