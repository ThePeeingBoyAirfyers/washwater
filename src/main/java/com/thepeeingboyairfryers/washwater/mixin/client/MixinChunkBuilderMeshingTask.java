package com.thepeeingboyairfryers.washwater.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSection;
import com.thepeeingboyairfryers.washwater.duck.ILevelSliceFluidSections;
import net.caffeinemc.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import net.caffeinemc.mods.sodium.client.world.LevelSlice;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkBuilderMeshingTask.class)
public class MixinChunkBuilderMeshingTask {


    @Redirect(
            method = "execute(Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildContext;Lnet/caffeinemc/mods/sodium/client/util/task/CancellationToken;)Lnet/caffeinemc/mods/sodium/client/render/chunk/compile/ChunkBuildOutput;",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/material/FluidState;isEmpty()Z"))
    boolean hasFluid(FluidState instance, @Local(name = "blockPos") BlockPos.MutableBlockPos blockPos, @Local(name = "slice") LevelSlice slice) {
        FluidSection section = ((ILevelSliceFluidSections) (Object) slice).ww€getSectionFor(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        return section.getAllVolume(blockPos.getX(), blockPos.getY(), blockPos.getZ()) > 0;
    }

}
