package com.thepeeingboyairfryers.washwater.mixin.client;

import com.thepeeingboyairfryers.washwater.client.fluid_rendering.WashFluidRendererFactory;
import net.caffeinemc.mods.sodium.client.services.FluidRendererFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FluidRendererFactory.class)
public interface MixinFluidRendererFactory {

    /**
     * Very sad and unhappy
     */
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/caffeinemc/mods/sodium/client/services/Services;load(Ljava/lang/Class;)Ljava/lang/Object;"))
    private static Object instantiate(Class<?> clazz) {
        return new WashFluidRendererFactory();
    }
}
