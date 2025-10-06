package com.thepeeingboyairfryers.washwater.common.fluids;

import com.thepeeingboyairfryers.washwater.common.storage.attachment.FluidsIndexationAttachment;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

public class FluidManager {
    private FluidManager() {
        throw new IllegalStateException();
    }

    private static FluidsIndexationAttachment fluidsIndexation;

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
}
