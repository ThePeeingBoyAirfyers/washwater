package com.thepeeingboyairfryers.washwater.base.common;

import com.thepeeingboyairfryers.washwater.util.performance.PerTickMeasurements;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickMeter;
import com.thepeeingboyairfryers.washwater.util.performance.PerTickTimer;
import com.thepeeingboyairfryers.washwater.util.performance.StatsReporter;
import net.minecraft.network.chat.Component;

public class WWStats {

    private static final StatsReporter REPORTER = new StatsReporter();
    public static final PerTickTimer FLUID_TICKING = REPORTER.addMeasurements(new PerTickTimer("Fluid Ticking", 60 * 20 * 15));
    public static final PerTickMeasurements TICKED_SECTIONS = REPORTER.addMeasurements(new PerTickMeasurements("Active sections", 60 * 20 * 15, ""));
    public static final PerTickMeter FLUID_MISSES = REPORTER.addMeasurements(new PerTickMeter("Fluid misses", 60 * 20 * 15, " misses"));
    public static final PerTickMeter PACKET_MISSES = REPORTER.addMeasurements(new PerTickMeter("Packet misses", 60 * 20 * 15, " misses"));

    public static Component printReport() {
        var result = Component.literal("WWStats:\n");
        result.append(REPORTER.report());

        return result;
    }

    private WWStats() {
        throw new IllegalStateException("Utility class");
    }
}
