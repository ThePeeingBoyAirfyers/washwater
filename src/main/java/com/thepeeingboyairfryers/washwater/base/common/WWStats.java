package com.thepeeingboyairfryers.washwater.base.common;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import com.thepeeingboyairfryers.washwater.util.performance.StatsReporter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.concurrent.TimeUnit;

public class WWStats {
    public static final MetricRegistry REGISTRY = new MetricRegistry();
    public static final Histogram TICKED_SECTIONS = REGISTRY.histogram("ticked_sections", () -> new Histogram(
            new SlidingTimeWindowArrayReservoir(5, TimeUnit.MINUTES))
    );


    public static final Meter FLUID_MISSES = REGISTRY.meter("fluid_misses");
    public static final Meter PACKET_MISSES = REGISTRY.meter("packet_misses");
    private static final StatsReporter REPORTER = new StatsReporter();
    public static final PerTickTimer FLUID_TICKING = REPORTER.addMeasurements(new PerTickTimer("Fluid Ticking", 60 * 20 * 15));

    public static Component printReport() {
        var result = Component.literal("WWStats:\n");
        if (FLUID_MISSES.getOneMinuteRate() > 0.1) {
            result.append("Fluid Misses (in one minute): ");
            result.append(Component.literal(FLUID_MISSES.getOneMinuteRate() + "\n")
                    .withStyle(ChatFormatting.DARK_RED));
        }

        result.append("Ticking sections (mean of last 5 minutes): ");
        result.append(Component.literal(TICKED_SECTIONS.getSnapshot().getMean() + "\n")
                .withStyle(ChatFormatting.DARK_GREEN));

        result.append(REPORTER.report());


        return result;
    }

    private WWStats() {
        throw new IllegalStateException("Utility class");
    }
}
