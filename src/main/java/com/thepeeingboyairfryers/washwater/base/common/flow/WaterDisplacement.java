package com.thepeeingboyairfryers.washwater.base.common.flow;

import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidUtil;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.util.PseudoRandom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WaterDisplacement {

    public static final Direction[] directions = Direction.values();

    public static final int maxDisplacementRange = 8;

    public static boolean displaceFluids(Level level, BlockPos pos) {
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;
        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = createAndFillViableDirectionList(level, pos);
        Direction biasDir = null;

        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            Direction[] array =  PseudoRandom.getRandom3DDirectionArray();
            for (int i = 0; i < 6; i++) {
                Direction dire = array[i];
                if (viableDirections.contains(dire)) {
                    biasDir = dire;
                    break;
                }
            }
            Iterator<Direction> dirIterator = viableDirections.iterator();
            int theoreticalCut = remainder / viableDirections.size();
            int theoreticalCutUp = theoreticalCut + initialVolume % viableDirections.size();
            while (dirIterator.hasNext()) {
                Direction dir = dirIterator.next();
                int cut = theoreticalCut;
                //BIAS LOGIC
                if (dir == biasDir) {
                    cut = theoreticalCutUp;
                    biasDir = null;
                }

                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i))) {
                        break;
                    }
                    cutRemainder = (cutRemainder - FluidUtil.addVolume(level, pos.relative(dir, i).mutable(), type, cutRemainder));
                }
                if (cutRemainder > 0) {
                    dirIterator.remove();
                }
                int cutTransaction = cut - cutRemainder;
                remainder -= cutTransaction;
            }
        }
        return remainder == 0;
    }

    public static boolean checkIfCanDisplaceFluids(Level level, BlockPos pos) {
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;
        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = createAndFillViableDirectionList(level, pos);
        Direction biasDir = null;

        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            Direction[] array =  PseudoRandom.getRandom3DDirectionArray();
            for (int i = 0; i < 6; i++) {
                Direction dire = array[i];
                if (viableDirections.contains(dire)) {
                    biasDir = dire;
                    break;
                }
            }


            Iterator<Direction> dirIterator = viableDirections.iterator();
            int theoreticalCut = remainder / viableDirections.size();
            while (dirIterator.hasNext()) {
                Direction dir = dirIterator.next();
                int cut = theoreticalCut;
                //BIAS LOGIC
                if (dir == biasDir) {
                    cut += initialVolume % viableDirections.size();
                    biasDir = null;
                }
                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i))) {
                        break;
                    }
                    cutRemainder = (cutRemainder - FluidUtil.canAddVolume(level, pos.relative(dir, i).mutable(), type, cutRemainder));
                }
                if (cutRemainder > 0) {
                    dirIterator.remove();
                }
                int cutTransaction = cut - cutRemainder;
                remainder -= cutTransaction;
            }
        }
        return remainder == 0;
    }


    public static List<Direction> createAndFillViableDirectionList(Level level, BlockPos pos) {
        List<Direction> viableDirections = new ArrayList<>(0);

        for (Direction dir : directions) {
            if (!FluidUtil.isSolid(level, pos.relative(dir))) {
                viableDirections.add(dir);
            }
        }
        return viableDirections;
    }

}
