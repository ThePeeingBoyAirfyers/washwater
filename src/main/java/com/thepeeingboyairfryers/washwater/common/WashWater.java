package com.thepeeingboyairfryers.washwater.common;

import com.mojang.logging.LogUtils;
import com.thepeeingboyairfryers.washwater.common.block.WWBlocks;
import com.thepeeingboyairfryers.washwater.common.blockentity.WWBlockEntities;
import com.thepeeingboyairfryers.washwater.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.common.item.WWItems;
import com.thepeeingboyairfryers.washwater.common.packets.WWNetworking;
import com.thepeeingboyairfryers.washwater.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.tests.BucketTest;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import org.slf4j.Logger;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Mod(WashWater.MOD_ID)
public class WashWater {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "washwater";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public WashWater(IEventBus modEventBus, ModContainer modContainer) {

        WWBlocks.register(modEventBus);
        WWItems.register(modEventBus);
        WWBlockEntities.register(modEventBus);
        WWAttachments.register(modEventBus);
        FluidManager.register(modEventBus);
        FluidSectionManager.register(modEventBus);
        WWNetworking.register(modEventBus);
        modEventBus.addListener(this::registerTests);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void registerTests(RegisterGameTestsEvent event) {
        event.register(BucketTest.class);
    }

    public static ResourceLocation resource(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
