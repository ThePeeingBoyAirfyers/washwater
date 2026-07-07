package com.thepeeingboyairfryers.washwater.base.common.fluids;

import com.thepeeingboyairfryers.washwater.base.common.storage.attachment.FluidsIndexationAttachment;
import com.thepeeingboyairfryers.washwater.base.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.util.WWFluidState;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import static com.thepeeingboyairfryers.washwater.base.common.WaterInfo.VOLUME_PER_LEVEL;

public class FluidManager {
    private static FluidsIndexationAttachment fluidsIndexation;
    public static final StreamCodec<ByteBuf, FluidType> FLUID_STREAM_CODEC = ByteBufCodecs.SHORT.map(FluidManager::getFluidType, FluidManager::getFluidId);

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

    public static int lowestTick(ServerLevel level) {
        return 2;
    }

    public static FluidState dropinFluidState(FluidType fluidType) { // Think about what you have done
        return fluidsIndexation.getRelatedFluids(fluidType).stream().findAny().orElseThrow().defaultFluidState();
    }

    public static FluidState getFluidState(MultiFluidValue fluidValue) { //TODO multifluids
        if (fluidValue.isEmpty()) return Fluids.EMPTY.defaultFluidState();

        var entry = fluidValue.iterator().next();
        var fluidType = fluidsIndexation.getRelatedFluids(entry.fluidType()).stream().findAny().orElseThrow();
        FluidState base = fluidType.defaultFluidState();
        if (fluidType instanceof FlowingFluid f) {
            if (entry.volume() > 900)
                base = f.getSource(false);
            else
                base = f.getFlowing((entry.volume() / VOLUME_PER_LEVEL + 1), false);
        }

        return new WWFluidState(fluidValue, base);
    }

    public static BlockState getFluidBlockState(MultiFluidValue result) {
        return getFluidState(result).createLegacyBlock();
    }

    public static void setFluidsIndexation(FluidsIndexationAttachment iFluidsIndexation) {
        fluidsIndexation = iFluidsIndexation;
    }

    private static void levelLoaded(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level && level.getServer().overworld() == level) {
            setFluidsIndexation(level.getData(WWAttachments.FLUIDS_INDEXATION.get()));
        }
    }
}
