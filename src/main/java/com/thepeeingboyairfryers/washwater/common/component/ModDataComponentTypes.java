package com.thepeeingboyairfryers.washwater.common.component;

import com.mojang.serialization.Codec;
import com.thepeeingboyairfryers.washwater.common.WashWater;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.createDataComponents(WashWater.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> BUCKET_FILL_LEVEL = register("bucket_fill_level",
            builder -> builder.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name,
                                                                                          UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register (IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }

}
