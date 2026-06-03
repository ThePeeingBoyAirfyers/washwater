package com.thepeeingboyairfryers.washwater.collections;

import com.mojang.serialization.Codec;
import com.thepeeingboyairfryers.washwater.WashWater;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class WWDataComponentTypes {

    private WWDataComponentTypes() {
        throw new IllegalStateException("Utility class");
    }


    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, WashWater.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BUCKET_FILL_LEVEL = register("bucket_fill_level",
            builder -> builder.persistent(Codec.INT));

    private static <T> DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }

}
