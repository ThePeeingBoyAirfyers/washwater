package com.thepeeingboyairfryers.washwater.client;

import com.thepeeingboyairfryers.washwater.client.debug.DebugFluidRenderer;
import com.thepeeingboyairfryers.washwater.common.WashWater;
import com.thepeeingboyairfryers.washwater.common.WaterInfo;
import com.thepeeingboyairfryers.washwater.common.component.ModDataComponentTypes;
import com.thepeeingboyairfryers.washwater.common.item.WWItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import static com.thepeeingboyairfryers.washwater.common.item.WWItems.PRECISION_BUCKET;

@Mod(value = WashWater.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = WashWater.MOD_ID, value = Dist.CLIENT)
public class WashWaterClient {
    public WashWaterClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        DebugFluidRenderer.register(container.getEventBus());
        init();
    }

    public static void registerItemProperties() {
        ItemProperties.register(PRECISION_BUCKET.get(), ResourceLocation.parse("bucketlevel"), (itemStack, clientWorld, livingEntity, seed) -> {
            if (!itemStack.has(ModDataComponentTypes.BUCKET_FILL_LEVEL)) {
                itemStack.set(ModDataComponentTypes.BUCKET_FILL_LEVEL, 0);
                return 0f;
            }
            return (itemStack.get(ModDataComponentTypes.BUCKET_FILL_LEVEL) / (float) WaterInfo.VOLUME_PER_BLOCK);
        });
    }


    // This method is called during the client setup phase.
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        WashWater.LOGGER.info("HELLO FROM CLIENT SETUP");
        WashWater.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        registerItemProperties();
    }

    public void init() {
        WashWater.LOGGER.info("HELLO FROM CLIENT INITIALIZATION");
    }
}
