package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.base.common.flow.WaterDisplacement;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class MixinLevel {

    @Shadow
    @Final
    public boolean isClientSide;

    @Inject(
            at = @At("HEAD"),
            method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
            cancellable = true
    )
    private void doDisplacementCheck(BlockPos pos, BlockState newState, int flags, CallbackInfoReturnable<Boolean> cir) {
        if (FluidUtil.hasFluid((Level) (Object) this, pos)) {
            if (WaterDisplacement.checkIfCanDisplaceFluids((Level) (Object) this, pos)) {
                if (!this.isClientSide) {
                    WaterDisplacement.displaceFluids((Level) (Object) this, pos);
                }
            } else {
                cir.setReturnValue(false);
            }
        }

    }

}
