package com.thepeeingboyairfryers.washwater.mixin.common.fake_blockstates;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thepeeingboyairfryers.washwater.duck.IFakeRegistryObject;
import net.minecraft.core.MappedRegistry;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MappedRegistry.class)
public class MixinMappedRegistry {
    @WrapMethod(method = "getId(Ljava/lang/Object;)I")
    int wrap(Object value, Operation<Integer> original) {
        if (value instanceof IFakeRegistryObject<?> f) value = f.ww€getOG();
        return original.call(value);
    }
}
