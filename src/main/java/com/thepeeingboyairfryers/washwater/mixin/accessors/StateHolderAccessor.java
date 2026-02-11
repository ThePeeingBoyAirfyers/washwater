package com.thepeeingboyairfryers.washwater.mixin.accessors;

import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(StateHolder.class)
public interface StateHolderAccessor {

    @Accessor("values")
    Reference2ObjectArrayMap<Property<?>, Comparable<?>> getValues();

    @Accessor("propertiesCodec")
    MapCodec<?> getPropertiesCodec();
}
