package com.thepeeingboyairfryers.washwater.common.fluids;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.scheduling.FluidTicker;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.common.util.DirectionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;

public class FluidUtil {
    public static final FluidType WATER_TYPE = NeoForgeMod.WATER_TYPE.value();
    public static final short VOLUME_OF_BLOCK = 1000;

    private FluidUtil() {
        throw new IllegalStateException();
    }

    public static boolean addVolume(Level level, BlockPos pos, FluidType type, int volume, boolean real) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight()) return false;
        if (volume == 0) return false;
        short oldVolume = getAllVolume(level, pos);
        boolean success = true;
        //System.out.println("pos: " + pos);
        WashWater.LOGGER.warn("pos: " + pos);
        if (FluidUtil.isSolid(level, pos)) {
            //System.out.println("returned false");
            WashWater.LOGGER.warn("Returned false");
            return false;
        }
        if (oldVolume < 0) {
            WashWater.LOGGER.warn("Tried to add water volume to a non-air block");
            return false;
        }

        short spaceLeft = (short) (VOLUME_OF_BLOCK - oldVolume);

        int newVolume = oldVolume + volume;
        if (spaceLeft > 0) {
            if (real) {
                var chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
                var fluidChunk = FluidSectionManager.getAttachmentFor(chunk);
                fluidChunk.addFluidVolume(
                        pos.getX(), pos.getY(), pos.getZ(),
                        type, (short) Math.min(spaceLeft, volume)
                );
                FluidTicker.tickFluid((ServerLevel) level, pos);
            }
        }

        if (spaceLeft < volume) {
            success = addVolume(level, pos.above(), type, volume - spaceLeft, real);
        }
        WashWater.LOGGER.warn("Returned: " + success);
        //System.out.println("returned true");
        return success;
    }

    public static int addWaterVolumeAndReturnRemaining(ServerLevel level, BlockPos pos, FluidType type, int volume, boolean doAddAbove) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight()) return volume;
        if (volume == 0) return volume;
        if (FluidUtil.isSolid(level, pos)) return volume;
        short oldVolume = getAllVolume(level, pos);
        if (oldVolume < 0) {
            WashWater.LOGGER.warn("Tried to add water volume to a non-air block");
            return volume;
        }
        int remainder;
        int newWaterVolume = oldVolume + volume;
        var chunk = level.getChunk(pos.getX() >> 4, pos.getZ() >> 4);
        var fluidChunk = FluidSectionManager.getAttachmentFor(chunk);
        if (newWaterVolume > VOLUME_OF_BLOCK) {
            fluidChunk.setVolume(
                    pos.getX(), pos.getY(), pos.getZ(),
                    MultiFluidValue.single(type, VOLUME_OF_BLOCK)
            );
            if (!doAddAbove)
                remainder = newWaterVolume - VOLUME_OF_BLOCK;
            else {
                remainder = addWaterVolumeAndReturnRemaining(level, pos.above(), type, newWaterVolume - VOLUME_OF_BLOCK, true);
            }
        } else {
            remainder = 0;
            fluidChunk.setVolume(
                    pos.getX(), pos.getY(), pos.getZ(),
                    MultiFluidValue.single(type, (short) newWaterVolume)
            );
            FluidTicker.tickFluid(level, pos.above());
        }
        FluidTicker.tickFluid(level, pos);
        return remainder;
    }

    public static int addWaterVolumeAndReturnRemainingImaginary(Level level, BlockPos pos, FluidType type, int volume, boolean doAddAbove) {
        if (pos.getY() < level.getMinBuildHeight() || pos.getY() > level.getMaxBuildHeight()) return volume;
        if (volume == 0) return volume;
        if (FluidUtil.isSolid(level, pos)) return volume;
        short oldVolume = getAllVolume(level, pos);
        if (oldVolume < 0) {
            WashWater.LOGGER.warn("Tried to add water volume to a non-air block");
            return volume;
        }
        int remainder;
        int newWaterVolume = oldVolume + volume;
        if (newWaterVolume > VOLUME_OF_BLOCK) {
            if (!doAddAbove)
                remainder = newWaterVolume - VOLUME_OF_BLOCK;
            else {
                remainder = addWaterVolumeAndReturnRemainingImaginary(level, pos.above(), type, newWaterVolume - VOLUME_OF_BLOCK, true);
            }
        } else {
            remainder = 0;
        }
        return remainder;
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
        return !level.getBlockState(pos).isAir() && !hasFluid(level, pos) ;
    }

}
