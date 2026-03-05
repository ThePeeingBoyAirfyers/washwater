package com.thepeeingboyairfryers.washwater.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.FluidChunkAttachment;
import com.thepeeingboyairfryers.washwater.duck.IChunkFluidSection;
import com.thepeeingboyairfryers.washwater.duck.IFakeRegistryObject;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LevelChunkSection.class, priority = 1200)
public class MixinLevelChunkSection implements IChunkFluidSection {
    @Unique
    private FluidSection ww€fluidSection;
    @Unique
    private FluidChunkAttachment.SectionUpdater ww€updateSection;

    @Unique
    public FluidSection ww€getFluidSection() {
        return this.ww€fluidSection;
    }

    @Unique
    @Override
    public void ww€setFluidSection(FluidSection fSection) {
        if (ww€fluidSection == fSection) return;
        this.ww€fluidSection = fSection;
        ww€updateSection.accept(fSection);
        ww€fluidSection.setContainer(this);
    }

    @Override
    @Unique
    public void markDirty() {
        ww€updateSection.markDirty();
    }

    @Unique
    @Override
    public void ww€configureFluidSectionUpdater(@NotNull FluidChunkAttachment.SectionUpdater updater) {
        ww€updateSection = updater;
    }


    @WrapMethod(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;")
    public BlockState ww€setBlockState(int x, int y, int z, BlockState state, boolean useLocks, Operation<BlockState> original) {
        if (ww€fluidSection != null)
            return original.call(x, y, z, FluidSectionManager.writeStateToFluidSection(ww€fluidSection, x, y, z, state), useLocks);

        if (state instanceof IFakeRegistryObject<?> f)
            state = (BlockState) f.ww€getOG();

        return original.call(x, y, z, state, useLocks);
    }

    @Inject(at = @At("RETURN"), method = "getBlockState", cancellable = true)
    public void ww€getBlockState(int x, int y, int z, CallbackInfoReturnable<BlockState> cir) {
        if (ww€fluidSection != null) {
            cir.setReturnValue(FluidSectionManager.getBlockStateFromFluidSection(ww€fluidSection, x, y, z, cir.getReturnValue()));
        }
    }

    @Inject(at = @At("RETURN"), method = "getFluidState", cancellable = true)
    public void ww€getFluidState(int x, int y, int z, CallbackInfoReturnable<FluidState> cir) {
        if (ww€fluidSection != null) {
            cir.setReturnValue(FluidSectionManager.getFluidStateFromFluidSection(ww€fluidSection, x, y, z, cir.getReturnValue()));
        }
    }

    @ModifyReturnValue(at = @At("RETURN"), method = "hasOnlyAir")
    public boolean hasOnlyAir(boolean original) {
        if (ww€fluidSection == null) return original;
        return original && ww€fluidSection.isEmpty();
    }
}
