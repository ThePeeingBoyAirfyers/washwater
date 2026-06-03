package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.base.common.flow.WaterPushing;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/BlockItem;placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z"), method = "place", cancellable = true)
    private void pushWaterAway(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (FluidUtil.hasFluid(level, pos)) {
            if (WaterPushing.checkIfCanDisplaceFluids(level, pos)) {
                if (!level.isClientSide) {
                    WaterPushing.displaceFluids((ServerLevel) level, pos);
                }
            } else {
                cir.setReturnValue(InteractionResult.FAIL);
            }
        }
    }
}

