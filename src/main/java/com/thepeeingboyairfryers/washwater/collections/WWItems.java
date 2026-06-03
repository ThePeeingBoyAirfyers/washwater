package com.thepeeingboyairfryers.washwater.collections;

import com.thepeeingboyairfryers.washwater.WashWater;
import com.thepeeingboyairfryers.washwater.gameplay.common.features.pipette.FluidPipetteItem;
import com.thepeeingboyairfryers.washwater.gameplay.common.features.bucket.PrecisionBucketItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WWItems {
    private WWItems() {
        throw new IllegalStateException("Utility class");
    }

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WashWater.MOD_ID);
    public static final DeferredItem<FluidPipetteItem> FLUID_PIPETTE = ITEMS.registerItem("fluid_pipette", FluidPipetteItem::new);
    public static final DeferredItem<PrecisionBucketItem> PRECISION_BUCKET = ITEMS.registerItem("precision_bucket", PrecisionBucketItem::new);

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
