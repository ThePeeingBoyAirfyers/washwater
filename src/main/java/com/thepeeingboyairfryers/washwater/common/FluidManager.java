package com.thepeeingboyairfryers.washwater.common;

import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import com.thepeeingboyairfryers.washwater.common.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class FluidManager {

    public static int tickSpeed(ServerLevel level) {
        return 2;
    }

    public static void addVolume(ServerLevel level, BlockPos pos, int volume) {
        int oldVolume = getVolume(level, pos);
        if (oldVolume < 0) {
            WashWater.LOGGER.warn("Tried to add water volume to a non-air block");
            return;
        }

        int newVolume = oldVolume + volume;
        if (newVolume > WaterInfo.volumePerBlock) {
            setVolume(level, pos, WaterInfo.volumePerBlock);
            addVolume(level, pos.above(), newVolume - WaterInfo.volumePerBlock);
        } else {
            setVolume(level, pos, newVolume);
        }
    }

    public static int getVolume(Level level, BlockPos pos) {
        return getVolume(level, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void setVolume(ServerLevel level, BlockPos pos, int volume) {
        setVolume(level, pos.getX(), pos.getY(), pos.getZ(), volume);
    }

    public static void setVolume(ServerLevel level, int x, int y, int z, int volume) {
        var chunk = level.getChunk(x >> 4, z >> 4);
        var fluidChunk = chunk.getData(WWAttachments.FLUID_CHUNK.get());
        fluidChunk.setWaterVolume(x & 15, y, z & 15, (short) volume);

        if (volume != 0) {
            FluidTicker.tickWater(level, x, y, z);
        }

        for (var direction : DirectionUtils.HORIZONTAL_TOP) {
            FluidTicker.tickIfWater(level, x + direction.getStepX(), y + direction.getStepY(), z + direction.getStepZ());
        }
    }

    public static int getVolume(Level level, BlockState state, int x, int y, int z) {
        return getVolume(
                level.getChunk(x >> 4, z >> 4),
                state,
                x, y, z
        );
    }

    public static int getVolume(Level level, int x, int y, int z) {
            return getVolume(
                    level.getChunk(x >> 4, z >> 4),
                    x, y, z
            );
    }

    public static int getVolume(LevelChunk chunk, int x, int y, int z) {
        return getVolume(
                chunk,
                chunk.getBlockState(new BlockPos(x, y, z)), //TODO im not a fan of this
                x, y, z
        );
    }

    public static int getVolume(LevelChunk chunk, BlockState state, int x, int y, int z) {
        var volume = WaterInfo.getWaterVolumeOfState(state);
        if (volume < 0) return volume;

        var fluidChunk = chunk.getData(WWAttachments.FLUID_CHUNK.get());
        return fluidChunk.getWaterVolume(x, y, z);
    }


}
