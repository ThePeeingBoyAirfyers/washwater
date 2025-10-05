package com.thepeeingboyairfryers.washwater.common.blockentity;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
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
        int vol0 = FluidUtil.getVolume(level, pos.below(), FluidUtil.WATER_TYPE);
        if (vol0 >= 0 && vol0 < 1000) {
            FluidUtil.setVolume((ServerLevel) level, pos.below(), MultiFluidValue.single(FluidUtil.WATER_TYPE, (short) 1000));
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            if (FluidUtil.isFilledUp(level, pos.relative(dir))) {
                FluidUtil.setVolume((ServerLevel) level, pos.relative(dir), MultiFluidValue.single(FluidUtil.WATER_TYPE, (short) 1000));
            }
        }

    }

}
