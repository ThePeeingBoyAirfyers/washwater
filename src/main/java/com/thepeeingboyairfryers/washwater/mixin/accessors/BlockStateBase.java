package com.thepeeingboyairfryers.washwater.mixin.accessors;

import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BlockBehaviour.BlockStateBase.class)
public interface BlockStateBase {

    @Accessor("cache")
    BlockBehaviour.BlockStateBase.Cache getCache();

    @Accessor("isRandomlyTicking")
    void setRandomlyTicking(boolean randomlyTicking);
}
