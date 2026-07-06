package com.thepeeingboyairfryers.washwater.gameplay.common.features.airblock;

import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.NeoForgeMod;
import org.jetbrains.annotations.NotNull;

public class WaterSourceAirBlock extends Block {

    public WaterSourceAirBlock(Properties properties) {
        super(properties);
    }

    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    protected void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        var fluids = FluidUtil.getFluids(level, pos);

        for (var entry : fluids) { //TODO temp check, cus of one type per block
            if (entry.fluidType() != NeoForgeMod.WATER_TYPE.value())
                return;
        }

        FluidUtil.addVolume(level, pos, NeoForgeMod.WATER_TYPE.value(), Config.WATER_SOURCE_GAIN.getAsInt());
    }

    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }
}
