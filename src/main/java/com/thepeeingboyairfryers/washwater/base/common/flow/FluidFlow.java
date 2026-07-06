package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class FluidFlow {
    private FluidType type;
    private FluidRegion region;
    private BlockPos selfPos;
    private MultiFluidValue self;
    private MultiFluidValue under;

    public void tick(FluidRegion iRegion, BlockPos pos) {
        this.region = iRegion;
        this.selfPos = pos;

        var iter = region.getFluids(selfPos).iterator();
        if (!iter.hasNext()) return;
        type = iter.next().fluidType();
        if (iter.hasNext()) return;

        self = region.getFluids(selfPos);
        int volume = self.forFluid(type);
        if (volume <= 0) return;

        int underY = selfPos.getY() - 1;
        under = region.getFluids(selfPos.getX(), underY, selfPos.getZ());
        var underVolume = under.forFluid(type);

        //Downwards flow
        if (!region.isSolid(selfPos.getX(), underY, selfPos.getZ()) && underVolume < WaterInfo.VOLUME_PER_BLOCK) {
            //Delete water at world bottom (void)
            if (underY == WaterInfo.MIN_Y) {
                region.setVolume(selfPos, fluid(0));
                return;
            }

            if (handleOtherFluid(selfPos.getX(), underY, selfPos.getZ()))
                return;

            //Flow down
            var transaction = Math.min(volume, WaterInfo.VOLUME_PER_BLOCK - underVolume);
            region.setVolume(selfPos, fluid(volume - transaction));
            region.setVolume(selfPos.getX(), underY, selfPos.getZ(), fluid(underVolume + transaction));
            volume -= transaction;
            //if (volume > 0) {
            //Flow downwards sideways
            //equalizeWaterDownwards(region, pos, volume);
            //}
        } else {
            //Flow sideways
            equalizeWater(volume);
        }
    }

    public void equalizeWater(int volume) {
        if (volume < WaterInfo.SURFACE_TENSION_LIMIT) return;
        int newVolume = volume;

        for (Direction direction : PseudoRandom.getRandomDirectionArray()) {
            int x = selfPos.getX() + direction.getStepX();
            int y = selfPos.getY();
            int z = selfPos.getZ() + direction.getStepZ();
            int otherVolume = region.getFluidVolume(x, y, z, type);

            if (region.isSolid(x, y, z)) continue;
            if (handleOtherFluid(x, y, z)) continue;

            int transfer = (newVolume - otherVolume) / WaterInfo.FLOW_DIVIDER;
            if (transfer > 2 || transfer < -2) {
                newVolume -= transfer;
                region.setVolume(x, y, z, fluid(otherVolume + transfer));
            }
        }

        if (newVolume != volume) region.setVolume(selfPos, fluid(newVolume));
    }

    private boolean handleOtherFluid(int x, int y, int z) {
        MultiFluidValue value = region.getFluids(x, y, z);
        if (value.getTotalVolume() == value.forFluid(type))
            return false;

        int lavaAmount = -1;
        if (type == WaterInfo.WATER_TYPE) {
            lavaAmount = value.forFluid(NeoForgeMod.LAVA_TYPE.value());
        } else if (type == NeoForgeMod.LAVA_TYPE.value()) {
            lavaAmount = self.forFluid(NeoForgeMod.LAVA_TYPE.value());
        }

        if (lavaAmount != -1) {
            region.setVolume(x, y, z, MultiFluidValue.EMPTY);
            region.setVolume(selfPos, MultiFluidValue.EMPTY);
            region.setState(x, y, z, lavaAmount > 900 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.STONE.defaultBlockState());
        }

        return true;
    }

    private MultiFluidValue fluid(int i) {
        return MultiFluidValue.single(type, (short) i);
    }
}
