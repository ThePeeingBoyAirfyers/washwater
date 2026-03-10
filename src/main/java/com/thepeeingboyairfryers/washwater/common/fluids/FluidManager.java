package com.thepeeingboyairfryers.washwater.common.fluids;

import com.thepeeingboyairfryers.washwater.common.storage.attachment.FluidsIndexationAttachment;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import static com.thepeeingboyairfryers.washwater.common.WaterInfo.VOLUME_PER_LEVEL;

public class FluidManager {
    private static FluidsIndexationAttachment fluidsIndexation;

    private FluidManager() {
        throw new IllegalStateException();
    }

    public static void register(IEventBus eventBus) {
        NeoForge.EVENT_BUS.addListener(FluidManager::levelLoaded);
    }

    public static int tickSpeed(ServerLevel level, FluidType fluidType) {
        return (fluidType.getViscosity() / 1000) + 1;
    }

    public static short getFluidId(@NotNull FluidType fluidType) {
        return fluidsIndexation.getId(fluidType);
    }

    public static @NotNull FluidType getFluidType(short id) {
        return fluidsIndexation.getFluid(id);
    }

    private static void levelLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.getServer().overworld() == level) {
            fluidsIndexation = level.getData(WWAttachments.FLUIDS_INDEXATION.get());
        }
    }

    public static int lowestTick(ServerLevel level) {
        return 2;
    }

    public static FluidState dropinFluidState(FluidType fluidType) { // Think about what you have done
        return fluidsIndexation.getRelatedFluids(fluidType).stream().findAny().orElseThrow().defaultFluidState();
    }

    public static FluidState getFluidState(MultiFluidValue result) {
        short vol = result.getTotalVolume(); //TODO multifluids

        if (vol == 0) {
            return Fluids.EMPTY.defaultFluidState();
        }

        return Fluids.FLOWING_WATER.getFlowing(((vol / VOLUME_PER_LEVEL) + 1), false);
    }

    public static BlockState getFluidBlockState(MultiFluidValue result) {
        return getFluidState(result).createLegacyBlock();
    }
}
