package com.thepeeingboyairfryers.washwater.client.fluid_rendering;

import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluids;
import net.caffeinemc.mods.sodium.client.model.quad.ModelQuad;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;

public class WaterSurfaceHandler {

    private final float[] heights = new float[6];
    private final BlockOcclusionCache occlusionCache = new BlockOcclusionCache();

    private LevelSlice level;
    private BlockPos blockPos;
    private BlockState blockState;
    private FluidState fluidState;

    private float northWestHeight;
    private float northEastHeight;
    private float southWestHeight;
    private float southEastHeight;
    private float selfHeight;

    public void configure(LevelSlice iLevel, BlockPos pos, BlockState state, FluidState iFluidState) {
        level = iLevel;
        blockPos = pos;
        blockState = state;
        fluidState = iFluidState;
        fetchHeights((ILevelSliceFluids) (Object) level);
    }

    public boolean configureFace(Direction dir, ModelQuad quad) {
        if (this.isFullBlockFluidOccluded(dir))
            return false;
        float yOffset = 0.00000001F;
        float c1, c2;
        float x1, x2;
        float z1, z2;
        switch (dir) {
            case NORTH:
                c1 = northWestHeight;
                c2 = northEastHeight;
                x1 = 0.0F;
                x2 = 1.0F;
                z1 = 0.001F;
                z2 = z1;
                break;

            case SOUTH:
                c1 = southEastHeight;
                c2 = southWestHeight;
                x1 = 1.0F;
                x2 = 0.0F;
                z1 = 0.999F;
                z2 = z1;
                break;
            case WEST:
                c1 = southWestHeight;
                c2 = northWestHeight;
                x1 = 0.001F;
                x2 = x1;
                z1 = 1.0F;
                z2 = 0.0F;
                break;
            case EAST:
                c1 = northEastHeight;
                c2 = southEastHeight;
                x1 = 0.999F;
                x2 = x1;
                z1 = 0.0F;
                z2 = 1.0F;
                break;

            case DOWN:
                setVertex(quad, 0, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f);
                setVertex(quad, 1, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);
                setVertex(quad, 2, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                setVertex(quad, 3, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
                return true;

            case UP:
                setVertex(quad, 0, 0.0f, southWestHeight, 1.0f, 0.0f, 0.0f);
                setVertex(quad, 1, 1.0f, southEastHeight, 1.0f, 1.0f, 0.0f);
                setVertex(quad, 2, 1.0f, northEastHeight, 0.0f, 1.0f, 1.0f);
                setVertex(quad, 3, 0.0f, northWestHeight, 0.0f, 0.0f, 1.0f);
                return true;
            default:
                throw new IllegalArgumentException();
        }

        //Horizontal Directions
        setVertex(quad, 0, x2, c2, z2, 1f, 1f);
        setVertex(quad, 1, x2, yOffset, z2, 1f, 1f);
        setVertex(quad, 2, x1, yOffset, z1, 1f, 1f);
        setVertex(quad, 3, x1, c1, z1, 1f, 1f);

        return true;
    }

    private void fetchHeights(ILevelSliceFluids fluids) {
        for (Direction dir : Direction.values()) {
            MultiFluidValue fluid = fluids.ww€getFluidFor(blockPos.getX() + dir.getStepX(), blockPos.getY() + dir.getStepY(), blockPos.getZ() + dir.getStepZ());
            heights[dir.ordinal()] = fluid.getTotalVolume() / (float) WaterInfo.VOLUME_PER_BLOCK;
        }

        MultiFluidValue fluid = fluids.ww€getFluidFor(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        selfHeight = fluid.getTotalVolume() / (float) WaterInfo.VOLUME_PER_BLOCK;

        this.northWestHeight = calculateCornerHeight(fluids, Direction.NORTH, Direction.WEST);
        this.northEastHeight = calculateCornerHeight(fluids, Direction.NORTH, Direction.EAST);
        this.southWestHeight = calculateCornerHeight(fluids, Direction.SOUTH, Direction.WEST);
        this.southEastHeight = calculateCornerHeight(fluids, Direction.SOUTH, Direction.EAST);
    }

    private void setVertex(ModelQuad quad, int i, float x, float y, float z, float u, float v) {
        quad.setX(i, x);
        quad.setY(i, y);
        quad.setZ(i, z);
        quad.setTexU(i, u);
        quad.setTexV(i, v);
    }

    private boolean isFullBlockFluidOccluded(Direction dir) {
        if (!this.occlusionCache.shouldDrawFullBlockFluidSide(blockState, level, blockPos, dir, fluidState, Shapes.block()))
            return true;
        if (dir == Direction.DOWN)
            return heights[dir.ordinal()] >= 0.9999f;
        if (dir == Direction.UP)
            return selfHeight >= 0.9999f && heights[dir.ordinal()] > 0;
        return heights[dir.ordinal()] > 0;
    }

    private float calculateCornerHeight(ILevelSliceFluids fluids, Direction dirA, Direction dirB) {
        int divisor = 1;
        float heightA = heights[dirA.ordinal()];
        float heightB = heights[dirB.ordinal()];
        MultiFluidValue fluidDiag = fluids.ww€getFluidFor(blockPos.getX() + dirA.getStepX() + dirB.getStepX(), blockPos.getY(), blockPos.getZ() + dirA.getStepZ() + dirB.getStepZ());
        MultiFluidValue fluidAAbove = fluids.ww€getFluidFor(blockPos.getX() + dirA.getStepX(), blockPos.getY() + 1, blockPos.getZ() + dirA.getStepZ());
        MultiFluidValue fluidBAbove = fluids.ww€getFluidFor(blockPos.getX() + dirB.getStepX(), blockPos.getY() + 1, blockPos.getZ() + dirB.getStepZ());
        MultiFluidValue fluidDiagAbove = fluids.ww€getFluidFor(blockPos.getX() + dirA.getStepX() + dirB.getStepX(), blockPos.getY() + 1, blockPos.getZ() + dirA.getStepZ() + dirB.getStepZ());
        float heightDiag = fluidDiag.getTotalVolume() / (float) WaterInfo.VOLUME_PER_BLOCK;

        if ((!fluidAAbove.isEmpty() && heightA >= 0.9999f) || (!fluidBAbove.isEmpty() && heightB >= 0.9999f) || (!fluidDiagAbove.isEmpty() && heightDiag >= 0.9999f))
            return 1.0f;

        if (heightA > 0)
            divisor++;
        if (heightB > 0)
            divisor++;
        if (heightDiag > 0)
            divisor++;

        return ((selfHeight + heightA + heightB + heightDiag) / (float) divisor);
    }

}
