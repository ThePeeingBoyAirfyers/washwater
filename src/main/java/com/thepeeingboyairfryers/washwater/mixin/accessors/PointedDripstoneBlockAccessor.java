package com.thepeeingboyairfryers.washwater.mixin.accessors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(PointedDripstoneBlock.class)
public interface PointedDripstoneBlockAccessor {

    @Invoker("isStalactiteStartPos")
    static boolean isStalactiteStartPos(BlockState state, LevelReader level, BlockPos pos) {
        return false;
    }

    @Invoker("findTip")
    static BlockPos findTip(BlockState state, LevelAccessor level, BlockPos pos, int maxIterations, boolean isTipMerge) {
        return pos;
    }

    @Invoker("isStalactite")
    static boolean isStalactite(BlockState state) {
        return false;
    }

    @Invoker("findRootBlock")
    static Optional<BlockPos> findRootBlock(Level level, BlockPos pos, BlockState state, int maxIterations) {
        return null;
    }


}
