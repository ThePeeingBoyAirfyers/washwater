package com.thepeeingboyairfryers.washwater.util.performance;

import com.thepeeingboyairfryers.washwater.Config;
import com.thepeeingboyairfryers.washwater.base.common.WWStats;
import com.thepeeingboyairfryers.washwater.collections.WWBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.Difficulty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.flat.FlatLayerInfo;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class WorldPerfTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldPerfTest.class);
    private static final int SIMULATION_DISTANCE = 16;
    private static int counter = 0;

    private WorldPerfTest() {
        throw new AssertionError();
    }

    public static void register(IEventBus modEventBus) {
        if (System.getProperty("ww.bench") == null && System.getProperty("ww.test") == null) return;
        if (System.getProperty("ww.bench") != null) {
            NeoForge.EVENT_BUS.addListener(WorldPerfTest::tickPost);
        }

        if (System.getProperty("ww.test") != null) {
            NeoForge.EVENT_BUS.addListener(WorldPerfTest::bucketDrop);
        }

        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(WorldPerfTest::screenOpening);
        }
    }

    private static void tickPost(ServerTickEvent.Post event) {
        if (counter++ >= 200) {
            counter = 0;
            event.getServer().sendSystemMessage(WWStats.printReport());
        }
    }

    private static void screenOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof TitleScreen)) return;
        try {
            FileUtils.deleteDirectory(new File("./saves/bench"));
        } catch (IOException e) {
            LOGGER.error("Failed to delete save file", e);
        }

        Minecraft mc = Minecraft.getInstance();
        mc.doRunTask(() -> flatworld(event.getNewScreen()));
    }

    private static void bucketDrop(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getInventory().add(new ItemStack(Items.WATER_BUCKET));
        event.getEntity().getInventory().add(new ItemStack(Items.LAVA_BUCKET));
    }

    private static void oldOverworld(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        WorldOpenFlows openFlows = mc.createWorldOpenFlows();
        openFlows.createFreshLevel("bench", new LevelSettings(
                        "bench",
                        GameType.CREATIVE,
                        false,
                        Difficulty.PEACEFUL,
                        true,
                        new GameRules(),
                        WorldDataConfiguration.DEFAULT
                ),
                new WorldOptions(
                        3820716206651411877L,
                        false,
                        false
                ), WorldPresets::createNormalWorldDimensions, screen);
    }

    private static void flatworld(Screen screen) {
        boolean test = System.getProperty("ww.test") != null;
        Minecraft mc = Minecraft.getInstance();
        WorldOpenFlows openFlows = mc.createWorldOpenFlows();
        Config.WATER_SOURCE_GAIN.set(150);

        var gameRules = new GameRules();
        gameRules.getRule(GameRules.RULE_RANDOMTICKING).set(test ? 0 : 3, null);

        openFlows.createFreshLevel("bench", new LevelSettings(
                        "bench",
                        GameType.CREATIVE,
                        false,
                        Difficulty.PEACEFUL,
                        true,
                        gameRules,
                        WorldDataConfiguration.DEFAULT
                ),
                new WorldOptions(
                        0L,
                        false,
                        false
                ), regs -> {
                    var conf = WorldPresets.createNormalWorldDimensions(regs);
                    var flatSettings = new FlatLevelGeneratorSettings(
                            Optional.empty(),
                            regs.registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS),
                            Collections.emptyList()
                    );

                    var list = flatSettings.getLayersInfo();
                    list.add(new FlatLayerInfo(1, Blocks.BEDROCK));
                    list.add(new FlatLayerInfo(2, Blocks.STONE));

                    if (!test) {
                        list.add(new FlatLayerInfo(2, Blocks.WATER));
                        list.add(new FlatLayerInfo(32, Blocks.AIR));
                        list.add(new FlatLayerInfo(16, WWBlocks.WATER_SOURCE_AIR_BLOCK.get()));
                    }
                    flatSettings.updateLayers();
                    flatSettings.setDecoration();

                    return conf.replaceOverworldGenerator(regs, new FlatLevelSource(flatSettings));
                }, screen);
    }
}
