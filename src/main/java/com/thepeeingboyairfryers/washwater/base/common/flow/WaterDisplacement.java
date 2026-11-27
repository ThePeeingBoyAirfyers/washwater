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

    private static final Direction[] DIRECTIONS = Direction.values();
    private static final int MAX_DISPLACEMENT_RANGE = 8; //TODO config

    private WaterDisplacement() {
        throw new AssertionError();
    }

    public static DisplacementPlan planFluidDisplacements(Level level, BlockPos pos) {
        List<DisplacementEntry> entries = new ArrayList<>();
        FluidType type = null;
        MultiFluidValue.Entry fluid = null;

        for (MultiFluidValue.Entry fluidEntry : FluidUtil.getFluids(level, pos)) {
            if (type != null) throw new IllegalStateException();
            type = fluidEntry.fluidType();
            fluid = fluidEntry;
        }
        int initialVolume = fluid.volume();
        List<Direction> viableDirections = findViableDirections(level, pos);
        int remainder = initialVolume;

        while (remainder > 0 && !viableDirections.isEmpty()) {
            Direction biasDir = PseudoRandom.getRandomElement(viableDirections);
            Iterator<Direction> dirIterator = viableDirections.iterator();
            int theoreticalCut = remainder / viableDirections.size();
            int theoreticalCutBias = theoreticalCut + (initialVolume % viableDirections.size());

            while (dirIterator.hasNext()) {
                Direction dir = dirIterator.next();
                int cut = dir == biasDir ? theoreticalCutBias : theoreticalCut;
                int cutRemainder = displaceInDirection(level, pos, entries, type, dir, cut);

                if (cutRemainder > 0) {
                    dirIterator.remove();
                }

                remainder -= cut - cutRemainder;
            }
        }

        return new DisplacementPlan(remainder, type, entries);
    }

    public static void applyDisplacements(Level level, DisplacementPlan plan) {
        for (DisplacementEntry entry : plan.entries) {
            int applied = FluidUtil.addVolume(level, new BlockPos(entry.x, entry.y, entry.z), plan.type, entry.displacement);
            if (applied < entry.displacement)
                throw new IllegalStateException("Displacement failed");
        }
    }

    private static List<Direction> findViableDirections(Level level, BlockPos pos) {
        List<Direction> viableDirections = new ArrayList<>(0);

        for (Direction dir : DIRECTIONS) {
            if (!FluidUtil.isSolid(level, pos.relative(dir))) {
                viableDirections.add(dir);
            }
        }
        return viableDirections;
    }

    private static int displaceInDirection(Level level, BlockPos pos, List<DisplacementEntry> entries, FluidType type, Direction dir, int cut) {
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        int cutRemainder = cut;
        for (int i = 1; i <= MAX_DISPLACEMENT_RANGE && cutRemainder > 0; i++) {
            mutablePos.set(pos).move(dir, i);
            if (FluidUtil.isSolid(level, mutablePos)) break;
            int used = FluidUtil.canAddVolume(level, mutablePos, type, cutRemainder);

            if (used > 0) {
                entries.add(new DisplacementEntry(mutablePos.getX(), mutablePos.getY(), mutablePos.getZ(), used));
                cutRemainder -= used;
            }
        }

        return cutRemainder;
    }

    public record DisplacementEntry(int x, int y, int z, int displacement) { }
    public record DisplacementPlan(int remainder, FluidType type, List<DisplacementEntry> entries) { }
}
