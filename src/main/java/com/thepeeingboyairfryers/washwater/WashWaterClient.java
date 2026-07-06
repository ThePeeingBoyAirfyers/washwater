package com.thepeeingboyairfryers.washwater;

import com.thepeeingboyairfryers.washwater.base.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.collections.WWDataComponentTypes;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.thepeeingboyairfryers.washwater.collections.WWItems.PRECISION_BUCKET;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Mod(value = WashWater.MOD_ID, dist = Dist.CLIENT)
public class WashWaterClient {
    public WashWaterClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener((FMLClientSetupEvent e) -> registerItemProperties());
    }

    public static void registerItemProperties() {
        ItemProperties.register(PRECISION_BUCKET.get(), ResourceLocation.parse("bucketlevel"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (!itemStack.has(WWDataComponentTypes.BUCKET_FILL_LEVEL)) {
                itemStack.set(WWDataComponentTypes.BUCKET_FILL_LEVEL, 0);
                return 0f;
            }

            //noinspection DataFlowIssue
            return (itemStack.get(WWDataComponentTypes.BUCKET_FILL_LEVEL) / (float) WaterInfo.VOLUME_PER_BLOCK);
        });
    }
}
