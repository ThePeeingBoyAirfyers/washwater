package com.thepeeingboyairfryers.washwater.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluidSections;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluidRendererCache {
    private final FluidSection[] prepared = new FluidSection[4];
    private final Direction[] prepDirs = new Direction[3];
    private final BlockPos.MutableBlockPos[] poses = new BlockPos.MutableBlockPos[]
            {new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos(), new BlockPos.MutableBlockPos()};
    private int prepLength = 0;
    private int currPos = 0;

    public void prepare(LevelSlice level, BlockPos pos) {
        int prepId = 0;
        int sX = pos.getX() >> 4;
        int sY = pos.getY() >> 4;
        int sZ = pos.getZ() >> 4;

        prepared[0] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX, sY, sZ);
        poses[0].set(pos);

        if (pos.getX() == 0) {
            prepDirs[prepId] = Direction.WEST;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX - 1, sY, sZ);
            poses[prepId].setWithOffset(pos, -1, 0, 0);
        } else if (pos.getX() == 15) {
            prepDirs[prepId] = Direction.EAST;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX + 1, sY, sZ);
            poses[prepId].setWithOffset(pos, 1, 0, 0);
        }

        if (pos.getY() == 0) {
            prepDirs[prepId] = Direction.DOWN;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX, sY - 1, sZ);
            poses[prepId].setWithOffset(pos, 0, -1, 0);
        } else if (pos.getY() == 15) {
            prepDirs[prepId] = Direction.UP;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX, sY + 1, sZ);
            poses[prepId].setWithOffset(pos, 0, 1, 0);
        }

        if (pos.getZ() == 0) {
            prepDirs[prepId] = Direction.NORTH;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX, sY, sZ - 1);
            poses[prepId].setWithOffset(pos, 0, 0, -1);
        } else if (pos.getZ() == 15) {
            prepDirs[prepId] = Direction.SOUTH;
            prepared[++prepId] = ((ILevelSliceFluidSections) (Object) level).ww€getSectionFor(sX, sY, sZ + 1);
            poses[prepId].setWithOffset(pos, 0, 0, 1);
        }

        prepLength = prepId;
    }

    // Uh not so good code :pensive:
    public void run(BiConsumer<Consumer<Direction>, Supplier<MultiFluidValue>> run) {
        if (prepared[0] != null) {
            synchronized (prepared[0]) {
                if (prepared[1] != null) {
                    synchronized (prepared[1]) {
                        if (prepared[2] != null) {
                            synchronized (prepared[2]) {
                                if (prepared[3] != null) {
                                    synchronized (prepared[3]) {
                                        run.accept(this::setNeighbor, this::getFluids);
                                    }
                                } else {
                                    run.accept(this::setNeighbor, this::getFluids);
                                }
                            }
                        } else {
                            run.accept(this::setNeighbor, this::getFluids);
                        }
                    }
                } else {
                    run.accept(this::setNeighbor, this::getFluids);
                }
            }
        } else {
            throw new IllegalStateException("No prepared FluidSections?");
        }
    }

    private MultiFluidValue getFluids() {
        BlockPos p = poses[currPos];
        return prepared[currPos].getVolume(p.getX(), p.getY(), p.getZ());
    }

    private void setNeighbor(Direction direction) {
        if (direction == null) {
            currPos = 0;
        } else {
            for (int i = 0; i < prepLength; i++) {
                if (prepDirs[i] == direction) {
                    currPos = i + 1;
                    return;
                }
            }
        }
    }
}
