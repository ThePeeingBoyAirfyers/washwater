package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.CollectingNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CollectingNeighborUpdater.class)
public class MixinNeighborUpdater {

    @Shadow
    @Final
    private Level level;

    @Inject(
            method = "neighborChanged(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;)V",
            at = @At(value = "HEAD")
    )
    public void tickWater(BlockPos pos, Block neighborBlock, BlockPos neighborPos, CallbackInfo ci) {
        if (level.isClientSide) return;
        FluidTicker.tickIfFluid((ServerLevel) this.level, pos.getX(), pos.getY(), pos.getZ());
    }

    @Inject(
            method = "neighborChanged(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/Block;Lnet/minecraft/core/BlockPos;Z)V",
            at = @At(value = "HEAD")
    )
    public void tickWater(BlockState state, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston, CallbackInfo ci) {
        if (level.isClientSide) return;
        FluidTicker.tickIfFluid((ServerLevel) this.level, pos.getX(), pos.getY(), pos.getZ());
    }

    @Inject(
            method = "updateNeighborsAtExceptFromFacing",
            at = @At(value = "HEAD")
    )
    public void tickWater(BlockPos pos, Block block, Direction facing, CallbackInfo ci) {
        if (level.isClientSide) return;
        for (var direction : Direction.values()) {
            FluidTicker.tickIfFluid((ServerLevel) this.level, pos.getX() + direction.getStepX(), pos.getY() + direction.getStepY(), pos.getZ() + direction.getStepZ());
        }
    }
}
