package com.thepeeingboyairfryers.washwater.util;

import com.mojang.serialization.MapCodec;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.base.common.fluids.MultiFluidValue;
import com.thepeeingboyairfryers.washwater.ducks.IFakeRegistryObject;
import com.thepeeingboyairfryers.washwater.mixin.accessors.BlockStateBase;
import com.thepeeingboyairfryers.washwater.mixin.accessors.StateHolderAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class WWBlockState extends BlockState implements IFakeRegistryObject<BlockState> {
    private final MultiFluidValue fluid;
    private final BlockState og;

    public WWBlockState(MultiFluidValue iFluid, BlockState iOg) {
        super(iOg.getBlock(), ((StateHolderAccessor) iOg).getValues(), (MapCodec<BlockState>) ((StateHolderAccessor) iOg).getPropertiesCodec());
        if (iFluid.isEmpty()) throw new IllegalArgumentException();

        this.og = iOg;
        this.fluid = iFluid;

        this.cache = ((BlockStateBase) iOg).getCache();
        ((BlockStateBase) this).setRandomlyTicking(iOg.isRandomlyTicking());
    }

    public MultiFluidValue getFluid() {
        return fluid;
    }

    @Override
    public @NotNull FluidState getFluidState() {
        return FluidManager.getFluidState(fluid);
    }

    @Override
    public <T extends Comparable<T>, V extends T> BlockState setValue(Property<T> property, V value) {
        return new WWBlockState(fluid, og.setValue(property, value));
    }

    @Override
    public BlockState ww€getOG() {
        return og;
    }
}
