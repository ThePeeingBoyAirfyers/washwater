package com.thepeeingboyairfryers.washwater.base.common.fluids;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class FluidUtil {
    public static final FluidType WATER_TYPE = NeoForgeMod.WATER_TYPE.value();
    public static final short VOLUME_OF_BLOCK = 1000;

    private FluidUtil() {
        throw new IllegalStateException();
    }

    public static int addVolume(Level level, BlockPos pos, FluidType type, int volume) {

        if (canAddVolume(level, pos, type, volume) == 0) {
            return 0;
        }

        short oldVolume = getAllVolume(level, pos);
        short spaceLeft = (short) (VOLUME_OF_BLOCK - oldVolume);
        int transaction = Math.min(volume, spaceLeft);
        var chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        var fluidChunk = FluidSectionManager.getAttachmentFor(chunk);

        fluidChunk.addFluidVolume(
                pos.getX(), pos.getY(), pos.getZ(),
                type, (short) Math.min(spaceLeft, volume)
        );
        FluidTicker.tickFluid((ServerLevel) level, pos);
        if (transaction < volume) {
            transaction += addVolume(level, pos.above(), type, volume - transaction);
        }
        return transaction;
    }

    public static int canAddVolume(Level level, BlockPos pos, FluidType type, int volume) {

        if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight()) return 0;
        if (volume == 0) return 0;
        if (FluidUtil.isSolid(level, pos)) return 0;

        short oldVolume = getAllVolume(level, pos);
        if (oldVolume < 0) {
            WashWater.LOGGER.warn("Tried to add water volume to a non-air block");
            return 0;
        }

        short spaceLeft = (short) (VOLUME_OF_BLOCK - oldVolume);
        int transaction = Math.min(volume, spaceLeft);
        if (transaction < volume) {
            transaction += canAddVolume(level, pos.above(), type, volume - transaction);
        }
        return transaction;
    }

    public static short getAllVolume(Level level, BlockPos pos) {
        return getAllVolume(level, pos.getX(), pos.getY(), pos.getZ());
    }

    public static short getAllVolume(Level level, int x, int y, int z) {
        if (y < level.getMinBuildHeight() || y > level.getMaxBuildHeight()) return 0;
        var chunk = level.getChunk(x >> 4, z >> 4);
        var fluidChunk = FluidSectionManager.getAttachmentFor(chunk);
        return fluidChunk.getAllVolume(x, y, z);
    }

    public static short getVolume(Level level, BlockPos pos, FluidType type) {
        return getVolume(level, pos.getX(), pos.getY(), pos.getZ(), type);
    }

    public static void setVolume(ServerLevel level, BlockPos pos, MultiFluidValue fluids) {
        setVolume(level, pos.getX(), pos.getY(), pos.getZ(), fluids);
    }

    public static void setVolume(ServerLevel level, int x, int y, int z, MultiFluidValue fluids) {
        if (y < level.getMinBuildHeight() || y > level.getMaxBuildHeight()) return;
        var chunk = level.getChunk(x >> 4, z >> 4);
        var fluidChunk = FluidSectionManager.getAttachmentFor(chunk);
        fluidChunk.setVolume(x & 15, y, z & 15, fluids);

        if (!fluids.isEmpty()) {
            FluidTicker.tickFluid(level, x, y, z);
        }

        for (var direction : DirectionUtils.HORIZONTAL_TOP) {
            FluidTicker.tickIfFluid(level, x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
        }
    }

    public static short getVolume(Level level, int x, int y, int z, FluidType type) {
        return getVolume(
                level.getChunk(x >> 4, z >> 4),
                x, y, z,
                type
        );
    }

    public static short getVolume(LevelChunk chunk, int x, int y, int z, FluidType type) {
        if (y < chunk.getMinBuildHeight() || y > chunk.getMaxBuildHeight()) return 0;
        return FluidSectionManager.getAttachmentFor(chunk).getVolume(x, y, z, type);
    }

    public static boolean isFilledUp(Level level, BlockPos pos) {
        return getAllVolume(level, pos) >= VOLUME_OF_BLOCK;
    }

    public static boolean hasFluid(Level level, BlockPos pos) {
        return getAllVolume(level, pos.getX(), pos.getY(), pos.getZ()) > 0;
    }

    public static boolean hasFluid(Level level, int x, int y, int z) {
        return getAllVolume(level, x, y, z) > 0;
    }

    public static boolean isSolid(Level level, BlockPos pos) {
        return !level.getBlockState(pos).isAir() && !hasFluid(level, pos);
    }

    public static MultiFluidValue getFluids(@NotNull Level level, @NotNull BlockPos pos) {
        return getFluids(
                level.getChunk(pos.getX() >> 4, pos.getZ() >> 4),
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
    }

    public static MultiFluidValue getFluids(@NotNull LevelChunk chunk, int x, int y, int z) {
        return FluidSectionManager.getAttachmentFor(chunk).getFluids(x, y, z);
    }
}
