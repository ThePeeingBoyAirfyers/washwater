package com.thepeeingboyairfryers.washwater.base.common;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SlidingTimeWindowArrayReservoir;
import com.codahale.metrics.Timer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.concurrent.TimeUnit;

public class WWStats {
    public static final MetricRegistry REGISTRY = new MetricRegistry();
    public static final Histogram TICKED_SECTIONS = REGISTRY.histogram("ticked_sections", () -> new Histogram(
            new SlidingTimeWindowArrayReservoir(5, TimeUnit.MINUTES))
    );

    public static final Timer FLUID_TICKING = REGISTRY.timer("fluid_ticking");
    public static final Meter FLUID_MISSES = REGISTRY.meter("fluid_misses");

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

        result.append("Time used for fluidsim:\n");
        result.append(Component.literal("    Mean  :" + FLUID_TICKING.getSnapshot().getMean() + "\n")
                .withStyle(ChatFormatting.GREEN));
        result.append(Component.literal("    95%   :" + FLUID_TICKING.getSnapshot().get95thPercentile() + "\n")
                .withStyle(ChatFormatting.RED));
        result.append(Component.literal("    99%   :" + FLUID_TICKING.getSnapshot().get99thPercentile() + "\n")
                .withStyle(ChatFormatting.DARK_RED));
        result.append(Component.literal("    99.9% :" + FLUID_TICKING.getSnapshot().get99thPercentile() + "\n")
                .withStyle(ChatFormatting.DARK_PURPLE));


        return result;
    }

    private WWStats() {
        throw new IllegalStateException("Utility class");
    }
}
