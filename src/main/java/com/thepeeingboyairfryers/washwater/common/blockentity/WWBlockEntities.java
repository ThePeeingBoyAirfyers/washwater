package com.thepeeingboyairfryers.washwater.common.blockentity;

import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.block.WWBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WWBlockEntities {
    private WWBlockEntities() {
        throw new IllegalStateException("Utility class");
    }

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, WashWater.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CosmicMonoxideSpoutBlockEntity>> COSMIC_MONOXIDE_SPOUT = BLOCK_ENTITY_TYPE.register(
            "cosmic_monoxide_spout",
            () -> BlockEntityType.Builder.of(
                CosmicMonoxideSpoutBlockEntity::new,
                WWBlocks.COSMIC_MONOXIDE_SPOUT.get()
            ).build(null)
    );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FathomlessFluidChasmBlockEntity>> FATHOMLESS_FLUID_CHASM = BLOCK_ENTITY_TYPE.register(
            "fathomless_fluid_chasm",
            () -> BlockEntityType.Builder.of(
                    FathomlessFluidChasmBlockEntity::new,
                    WWBlocks.FATHOMLESS_FLUID_CHASM.get()
            ).build(null)
    );

    public static void register(net.neoforged.bus.api.IEventBus bus) {
        BLOCK_ENTITY_TYPE.register(bus);
    }
}
