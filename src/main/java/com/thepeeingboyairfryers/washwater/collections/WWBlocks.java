package com.thepeeingboyairfryers.washwater.collections;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.gameplay.common.features.airblock.WaterSourceAirBlock;
import com.thepeeingboyairfryers.washwater.gameplay.common.features.infinite.CosmicMonoxideSpoutBlock;
import com.thepeeingboyairfryers.washwater.gameplay.common.features.infinite.FathomlessFluidChasmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WWBlocks {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(WashWater.MOD_ID);
    public static final DeferredBlock<CosmicMonoxideSpoutBlock> COSMIC_MONOXIDE_SPOUT = BLOCKS.registerBlock("cosmic_monoxide_spout", CosmicMonoxideSpoutBlock::new);
    public static final DeferredBlock<FathomlessFluidChasmBlock> FATHOMLESS_FLUID_CHASM = BLOCKS.registerBlock("fathomless_fluid_chasm", FathomlessFluidChasmBlock::new);
    public static final DeferredBlock<WaterSourceAirBlock> WATER_SOURCE_AIR_BLOCK = BLOCKS.registerBlock("water_source_air_block", WaterSourceAirBlock::new, BlockBehaviour.Properties.of().replaceable().noCollission().noLootTable().air().randomTicks());

    private WWBlocks() {
        throw new IllegalStateException("Utility class");
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
