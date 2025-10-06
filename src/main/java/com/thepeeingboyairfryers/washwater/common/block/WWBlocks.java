package com.thepeeingboyairfryers.washwater.common.block;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WWBlocks {
    private WWBlocks() {
        throw new IllegalStateException("Utility class");
    }

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WashWater.MOD_ID);

    public static final DeferredBlock<CosmicMonoxideSpoutBlock> COSMIC_MONOXIDE_SPOUT = BLOCKS.registerBlock("cosmic_monoxide_spout", CosmicMonoxideSpoutBlock::new);
    public static final DeferredBlock<FathomlessFluidChasmBlock> FATHOMLESS_FLUID_CHASM = BLOCKS.registerBlock("fathomless_fluid_chasm", FathomlessFluidChasmBlock::new);

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
