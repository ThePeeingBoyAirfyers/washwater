package com.thepeeingboyairfryers.washwater.mixin.accessors;

import net.caffeinemc.mods.sodium.client.render.chunk.compile.executor.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ChunkBuilder.class)
public interface ChunkBuilderAccessor {

    @Accessor
    List<Thread> getThreads();
}
