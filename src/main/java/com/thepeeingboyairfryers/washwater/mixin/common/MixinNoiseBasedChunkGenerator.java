package com.thepeeingboyairfryers.washwater.mixin.common;

import com.thepeeingboyairfryers.washwater.common.block.WWBlocks;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NoiseBasedChunkGenerator.class) // TODO TFC uses their own gen and we should intercept there too
public class MixinNoiseBasedChunkGenerator {

    @Redirect(method = "doFill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;"))
    BlockState ww€sourceify(LevelChunkSection instance, int x, int y, int z, BlockState state, boolean lock) {
        if (state.is(Blocks.WATER)) state = WWBlocks.WATER_SOURCE_AIR_BLOCK.get().defaultBlockState();
        return instance.setBlockState(x, y, z, state, lock);
    }
}
