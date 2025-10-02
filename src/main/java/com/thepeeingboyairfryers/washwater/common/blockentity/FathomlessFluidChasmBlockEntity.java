package com.thepeeingboyairfryers.washwater.common.blockentity;

import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
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
        for (Direction dir : Direction.values()) {
            if (FluidUtil.getVolume(level, pos.relative(dir), FluidUtil.WATER_TYPE) > 0) {
                FluidUtil.setVolume((ServerLevel) level, pos.relative(dir), MultiFluidValue.EMPTY);
            }
        }

    }

}
