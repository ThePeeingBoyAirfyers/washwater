package com.thepeeingboyairfryers.washwater.util;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.ducks.IFakeRegistryObject;
import com.thepeeingboyairfryers.washwater.ducks.IFluidState;
import com.thepeeingboyairfryers.washwater.mixin.accessors.StateHolderAccessor;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class WWFluidState extends FluidState implements IFluidState, IFakeRegistryObject<FluidState> {
    private final MultiFluidValue fluid;
    private final FluidState og;

    public WWFluidState(MultiFluidValue iFluid, FluidState iOg) {
        super(
                iOg.getType(),
                ((StateHolderAccessor) iOg).getValues(),
                (MapCodec<FluidState>) ((StateHolderAccessor) iOg).getPropertiesCodec()
        );
        if (iFluid.isEmpty()) throw new IllegalArgumentException("Fluid is empty");

        this.fluid = iFluid;
        this.og = iOg;
    }

    @Override
    public @NotNull MultiFluidValue ww€getFluid() {
        return fluid;
    }

    @Override
    public FluidState ww€getOG() {
        return og;
    }
}
