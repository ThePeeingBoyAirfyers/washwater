package com.thepeeingboyairfryers.washwater.common.util;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.duck.IFakeRegistryObject;
import com.thepeeingboyairfryers.washwater.mixin.accessors.StateHolderAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class WWBlockState extends BlockState implements IFakeRegistryObject<BlockState> {
    private final MultiFluidValue fluid;
    private final BlockState og;

    public WWBlockState(MultiFluidValue iFluid, BlockState iOg) {
        super(iOg.getBlock(), ((StateHolderAccessor) iOg).getValues(), (MapCodec<BlockState>) ((StateHolderAccessor) iOg).getPropertiesCodec());
        if (iFluid.isEmpty()) throw new IllegalArgumentException();

        this.og = iOg;
        this.fluid = iFluid;

        initCache();
    }

    public MultiFluidValue getFluid() {
        return fluid;
    }

    @Override
    public @NotNull FluidState getFluidState() {
        return new WWFluidState(fluid, Fluids.WATER.defaultFluidState());
    }

    @Override
    public BlockState ww€getOG() {
        return og;
    }
}
