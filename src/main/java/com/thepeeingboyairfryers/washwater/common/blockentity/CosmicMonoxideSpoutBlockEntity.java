package com.thepeeingboyairfryers.washwater.common.blockentity;

import com.thepeeingboyairfryers.washwater.common.FluidManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CosmicMonoxideSpoutBlockEntity extends BlockEntity {

    public CosmicMonoxideSpoutBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(WWBlockEntities.COSMIC_MONOXIDE_SPOUT.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, CosmicMonoxideSpoutBlockEntity entity) {
        //DO TICK STUFF
        if (!level.isClientSide) {
            performSpoutAction(level, pos);
/*            if (FluidTicker.shouldTick((ServerLevel) level)) {
                performSpoutAction(level, pos);
            }*/
        }
    }

    public static void performSpoutAction(Level level, BlockPos pos) {
        int vol0 = FluidManager.getVolume(level, pos.below());
        if (vol0 >= 0 && vol0 < 1000) {
            FluidManager.setVolume((ServerLevel) level, pos.below(), 1000);
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            int vol1 = FluidManager.getVolume(level, pos.relative(dir));
            if (vol1 >= 0 && vol1 < 1000) {
                FluidManager.setVolume((ServerLevel) level, pos.relative(dir), 1000);
            }
        }

    }

}
