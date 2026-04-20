package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.common.flow.WaterPushing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class MixinPistonBaseBlock {

    @Inject(at = @At("HEAD"), method = "moveBlocks", cancellable = true)
    private void moveBlocks(Level level, BlockPos blockPos, Direction direction, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        if (!level.isClientSide) {
            if (WaterPushing.checkIfCanPushWater((ServerLevel) level, blockPos, direction)) {
                BlockPos blockPos2 = blockPos.relative(direction);
                boolean returnValue = WaterPushing.tryPushWater((ServerLevel) level, blockPos, direction);
                if (returnValue == true) {
                    level.setBlock(blockPos2, Blocks.AIR.defaultBlockState(), 11);;
                }
            } else {
                cir.setReturnValue(false);
                cir.cancel();
            }

        }
    }
}