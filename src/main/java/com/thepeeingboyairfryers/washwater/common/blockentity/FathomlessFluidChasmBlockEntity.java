package com.thepeeingboyairfryers.washwater.common.blockentity;

import com.thepeeingboyairfryers.washwater.common.FluidManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FathomlessFluidChasmBlockEntity extends BlockEntity {
    public FathomlessFluidChasmBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WWBlockEntities.FATHOMLESS_FLUID_CHASM.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FathomlessFluidChasmBlockEntity entity) {
        //DO TICK STUFF
        if (!level.isClientSide) {
            performChasmAction(level, pos);
/*            if (FluidTicker.shouldTick((ServerLevel) level)) {
                performSpoutAction(level, pos);
            }*/
        }
    }

    public static void performChasmAction(Level level, BlockPos pos) {
        if (FluidManager.getVolume(level, pos.below()) > 0) {
            FluidManager.setVolume((ServerLevel) level, pos.below(), 0);
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (FluidManager.getVolume(level, pos.relative(dir)) > 0) {
                FluidManager.setVolume((ServerLevel) level, pos.relative(dir), 0);
            }
        }

    }

}
