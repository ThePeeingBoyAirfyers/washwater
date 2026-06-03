package com.thepeeingboyairfryers.washwater.mixin.common.fake_blockstates;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.thepeeingboyairfryers.washwater.ducks.IFakeRegistryObject;
import net.minecraft.util.CrudeIncrementalIntIdentityHashBiMap;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CrudeIncrementalIntIdentityHashBiMap.class)
public class MixinCrudeIncrementalIntIdentityHashBiMap {
    @WrapMethod(method = "getId")
    int wrap(Object value, Operation<Integer> original) {
        if (value instanceof IFakeRegistryObject<?> f) value = f.ww€getOG();
        return original.call(value);
    }
}
