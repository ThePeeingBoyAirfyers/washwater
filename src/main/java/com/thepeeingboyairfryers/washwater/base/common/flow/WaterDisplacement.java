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
import java.util.ListIterator;

public class WaterDisplacement {

    public static final Direction[] directions = Direction.values();

    public static final int maxDisplacementRange = 8;


/*    public static boolean displaceFluids(Level level, BlockPos pos) {
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;
        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = createAndFillViableDirectionList(level, pos);
        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            for (Direction dir : viableDirections) {
                int cut = remainder / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
                    cutRemainder = (cutRemainder - FluidUtil.addVolume(level, pos.relative(dir, i).mutable(), type, cutRemainder));
                }
                if (cutRemainder > 0) {
                    viableDirections.remove(dir);
                }
                int cutTransaction = cut - cutRemainder;
                remainder -= cutTransaction;
            }
        }
        return remainder == 0;

    }*/

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
        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            Iterator<Direction> dirIterator = viableDirections.iterator();
            System.out.println("viable dirs: " + viableDirections);
            System.out.println("remainder: " + remainder);
            while (dirIterator.hasNext()) {
                System.out.println("hasnext");
                Direction dir = dirIterator.next();
                int cut = remainder / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
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

/*    public static int fluidDisplacementLoop(Level level, BlockPos pos, int initialVolume, List<Direction> viableDirections, FluidType type) {
        int remainder = 0;
        if (initialVolume > 0) {
            for (Direction dir : viableDirections) {
                int cut = initialVolume / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                for (int i = 1; i <= maxDisplacementRange && cut > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
                    cut = (cut - FluidUtil.addVolume(level, pos.relative(dir, i).mutable(), type, cut));
                }
                remainder += cut;
            }
            return remainder;
        }
        return 0;
    }*/

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
        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            Iterator<Direction> dirIterator = viableDirections.iterator();
            System.out.println("viable dirs: " + viableDirections);
            System.out.println("remainder: " + remainder);
            while (dirIterator.hasNext()) {
                System.out.println("hasnext");
                Direction dir = dirIterator.next();
                int cut = remainder / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
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

/*    public static boolean checkIfCanDisplaceFluids(Level level, BlockPos pos) {
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;
        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = createAndFillViableDirectionList(level, pos);
        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            for (Direction dir : viableDirections) {
                int cut = remainder / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                int cutRemainder = cut;
                for (int i = 1; i <= maxDisplacementRange && cutRemainder > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
                    cutRemainder = (cutRemainder - FluidUtil.canAddVolume(level, pos.relative(dir, i).mutable(), type, cutRemainder));
                }
                if (cutRemainder > 0) {
                    viableDirections.remove(dir);
                }
                int cutTransaction = cut - cutRemainder;
                remainder -= cutTransaction;
            }
        }
        return remainder == 0;
    }*/

/*    public static boolean checkIfCanDisplaceFluids(Level level, BlockPos pos) {
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;
        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = createAndFillViableDirectionList(level, pos);
        int remainder = 0;
        System.out.println("initial volume = " + initialVolume);
        System.out.println("viable dirs = " + viableDirections);

        if (initialVolume > 0) {
            for (Direction dir : viableDirections) {
                int cut = initialVolume / viableDirections.size();
                if (dir == Direction.UP)
                    cut += initialVolume % viableDirections.size();
                for (int i = 1; i <= maxDisplacementRange && cut > 0; i++) {
                    if (FluidUtil.isSolid(level, pos.relative(dir, i)))
                        break;
                    cut = (cut - FluidUtil.canAddVolume(level, pos.relative(dir, i).mutable(), type, cut));
                    System.out.println("cut at end of dir " + cut);
                }
                System.out.println("remainder at end of dir: " + remainder);
                remainder += cut;
            }
            System.out.println("remainder at end: " + remainder);
            return remainder == 0;
        }
        return false;
    }*/


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
