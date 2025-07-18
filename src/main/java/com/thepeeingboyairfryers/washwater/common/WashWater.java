package com.thepeeingboyairfryers.washwater.common;

import com.mojang.logging.LogUtils;
import com.thepeeingboyairfryers.washwater.common.block.WWBlocks;
import com.thepeeingboyairfryers.washwater.common.item.WWItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
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

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation resource(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }
}
