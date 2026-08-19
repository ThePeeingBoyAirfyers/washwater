package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PointedDripstoneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Iterator;
import java.util.Optional;

import static com.thepeeingboyairfryers.washwater.mixin.accessors.PointedDripstoneBlockAccessor.*;

@Mixin(PointedDripstoneBlock.class)
public class MixinPointedDripstoneBlock {

    /**
     * @author SirWashington
     * @reason F### fluid duping
     */
    @Overwrite
    public static void maybeTransferFluid(BlockState state, ServerLevel level, BlockPos pos, float randChance) {
        if (isStalactiteStartPos(state, level, pos)) {
            Optional<BlockPos> rootFluidPosOptional = getFluidPosAbove(level, pos, state);
            if (rootFluidPosOptional.isEmpty()) {
                return;
            }

            BlockPos rootFluidPos = rootFluidPosOptional.get().above();
            MultiFluidValue fluidAboveStalactite = FluidUtil.getFluids(level, rootFluidPos);
            if (fluidAboveStalactite.isEmpty()) {
                return;
            }

            Iterator<MultiFluidValue.Entry> fluids = fluidAboveStalactite.iterator();
            MultiFluidValue.Entry fluid = fluids.next();
            if (fluids.hasNext()) throw new IllegalStateException();;

            FluidType type = fluid.fluidType();
            int fluidVolume = fluid.volume();

            //FluidType.DripstoneDripInfo dripInfo = fluid.getFluidType().getDripInfo();
            if (fluidVolume >= WaterInfo.DROPLET_SIZE) {
                if (randChance < 0.9F) {
                    BlockPos blockpos = findTip(state, level, pos, 11, false).below();
                    if (FluidUtil.canAddVolume(level, blockpos, type, WaterInfo.DROPLET_SIZE) == WaterInfo.DROPLET_SIZE) {
                        //remove consumed fluid from above
                        FluidUtil.setVolume(level, rootFluidPos, MultiFluidValue.single(type, (short) (fluidVolume - WaterInfo.DROPLET_SIZE)));
                        //place water droplet below
                        FluidUtil.addVolume(level, blockpos, type, WaterInfo.DROPLET_SIZE);
                    }
                }
            }
        }
    }

    private static Optional<BlockPos> getFluidPosAbove(Level level, BlockPos pos, BlockState state) {
        return !isStalactite(state) ? Optional.empty() : findRootBlock(level, pos, state, 11);
    }

}