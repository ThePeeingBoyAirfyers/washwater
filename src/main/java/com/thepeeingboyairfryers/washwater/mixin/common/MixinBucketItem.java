package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(BucketItem.class)
public abstract class MixinBucketItem {

    @Redirect(
            method = "emptyContents(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/BlockHitResult;Lnet/minecraft/world/item/ItemStack;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z"

            )
    )
    private boolean ww€setBlock(Level level, BlockPos pos, BlockState state, int flags) {
        if (!level.isClientSide && pos.getY() != WaterInfo.minY) {
            FluidUtil.addVolume((ServerLevel) level, pos, WaterInfo.WATER_TYPE, WaterInfo.volumePerBlock);
            return true;
        } else {
            return false;
        }
    }
}
