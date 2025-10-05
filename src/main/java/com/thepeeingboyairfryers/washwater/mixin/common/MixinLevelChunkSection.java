package com.thepeeingboyairfryers.washwater.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.duck.IChunkFluidSection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(value = LevelChunkSection.class, priority = 1200)
public class MixinLevelChunkSection implements IChunkFluidSection {
    @Unique
    private FluidSection ww€fluidSection;
    @Unique
    private Consumer<FluidSection> ww€updateSection;

    @Unique
    public FluidSection ww€getFluidSection() {
        return this.ww€fluidSection;
    }

    @Unique
    public void ww€setFluidSection(FluidSection fSection) {
        if (ww€fluidSection == fSection) return;
        this.ww€fluidSection = fSection;
        ww€updateSection.accept(fSection);
        ww€fluidSection.setContainer(this);
    }

    @Override
    public void ww€configureFluidSectionUpdater(@NotNull Consumer<FluidSection> updater) {
        ww€updateSection = updater;
    }

    @Inject(at = @At("HEAD"), method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;")
    public void setBlockState(int x, int y, int z, BlockState state, boolean lock, CallbackInfoReturnable<BlockState> cir) {
        if (ww€fluidSection != null)
            FluidSectionManager.writeStateToFluidSection(ww€fluidSection, x, y, z, state);
    }

    @Inject(at = @At("RETURN"), method = "getBlockState", cancellable = true)
    public void getBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> cir) {
        if (ww€fluidSection != null && cir.getReturnValue().isAir()) {
            cir.setReturnValue(FluidSectionManager.getBlockStateFromFluidSection(ww€fluidSection, x, y, z));
        }

    }

    @Inject(at = @At("RETURN"), method = "getFluidState", cancellable = true)
    public void getFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir) {
        if (ww€fluidSection != null && cir.getReturnValue().isEmpty()) {
            cir.setReturnValue(FluidSectionManager.getFluidStateFromFluidSection(ww€fluidSection, x, y, z));
        }
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "hasOnlyAir")
    public boolean hasOnlyAir(boolean original) {
        return original && (ww€fluidSection == null || ww€fluidSection.isEmpty());
    }
}
