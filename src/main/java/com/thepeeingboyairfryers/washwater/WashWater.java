package com.thepeeingboyairfryers.washwater;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.logging.LogUtils;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.base.common.fluids.FluidManager;
import com.thepeeingboyairfryers.washwater.base.common.scheduling.FluidTicker;
import com.thepeeingboyairfryers.washwater.base.common.storage.FluidSectionManager;
import com.thepeeingboyairfryers.washwater.base.common.storage.attachment.WWAttachments;
import com.thepeeingboyairfryers.washwater.collections.WWBlockEntities;
import com.thepeeingboyairfryers.washwater.collections.WWBlocks;
import com.thepeeingboyairfryers.washwater.collections.WWDataComponentTypes;
import com.thepeeingboyairfryers.washwater.collections.WWItems;
import com.thepeeingboyairfryers.washwater.base.common.WWNetworking;
import com.thepeeingboyairfryers.washwater.tests.BucketTest;
import com.thepeeingboyairfryers.washwater.util.performance.WorldPerfTest;
import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import org.slf4j.Logger;


@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Mod(WashWater.MOD_ID)
public class WashWater {
    public static final String MOD_ID = "washwater";
    public static final Logger LOGGER = LogUtils.getLogger();
    private static Spark spark;

    public WashWater(IEventBus modEventBus, ModContainer modContainer) {

        WWBlocks.register(modEventBus);
        WWItems.register(modEventBus);
        WWDataComponentTypes.register(modEventBus);
        WWBlockEntities.register(modEventBus);
        WWAttachments.register(modEventBus);
        WWNetworking.register(modEventBus);
        WorldPerfTest.register(modEventBus);

        FluidManager.register(modEventBus);
        FluidSectionManager.register(modEventBus);
        FluidTicker.register(modEventBus);

        modEventBus.addListener(this::registerTests);
        modEventBus.addListener(this::loadComplete);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation resource(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static Spark getSpark() {
        return spark;
    }

    private void registerTests(RegisterGameTestsEvent event) {
        event.register(BucketTest.class);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        try {
            spark = SparkProvider.get();
            LOGGER.info("Spark is installed and found.");
        } catch (NoClassDefFoundError e) {
            LOGGER.debug("Spark is not installed.");
        } catch (IllegalStateException e) {
            LOGGER.warn("Spark has not been initialized!, but could find the class?");
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(LiteralArgumentBuilder.<CommandSourceStack>literal("wwstats").executes((ctx -> {
            ctx.getSource().sendSuccess(WWStats::printReport, true);
            return 0;
        })));
    }

}
