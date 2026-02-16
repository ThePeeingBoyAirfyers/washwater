package com.thepeeingboyairfryers.washwater.common.util.performance;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldPerfTest {
    private WorldPerfTest() {
        throw new AssertionError();
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldPerfTest.class);
    private static final int SIMULATION_DISTANCE = 16;
    private static long startTime;


    public static void register(IEventBus modEventBus) {
        NeoForge.EVENT_BUS.addListener(WorldPerfTest::tickPost);
        NeoForge.EVENT_BUS.addListener(WorldPerfTest::tickPre);
        if (FMLEnvironment.dist.isClient()) {
            NeoForge.EVENT_BUS.addListener(WorldPerfTest::screenOpening);
        }
    }

    private static void tickPre(ServerTickEvent.Pre event) {
        startTime = System.nanoTime();
    }

    private static void tickPost(ServerTickEvent.Post event) {
        long delta = System.nanoTime() - startTime;
        LOGGER.info("{}ms{}", delta / 1000000, delta % 1000000);
    }

    private static void screenOpening(ScreenEvent.Opening event) {
        if (!(event.getNewScreen() instanceof TitleScreen)) return;

        Minecraft mc = Minecraft.getInstance();
        mc.doRunTask(() -> {
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
                    ), WorldPresets::createNormalWorldDimensions, event.getNewScreen());
        });
    }
}
