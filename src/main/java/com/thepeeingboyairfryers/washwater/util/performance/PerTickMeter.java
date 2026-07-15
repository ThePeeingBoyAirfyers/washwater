package com.thepeeingboyairfryers.washwater.util.performance;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class PerTickMeter extends PerTickMeasurements {
    private final AtomicInteger meter = new AtomicInteger(0);

    public PerTickMeter(String name, int iMaxMeasurements, String suffix) {
        super(name, iMaxMeasurements, suffix);
    }

    public PerTickMeter(String name, int iMaxMeasurements, Long2ObjectFunction<String> iFormatter) {
        super(name, iMaxMeasurements, iFormatter);
    }

    public void mark() {
        meter.incrementAndGet();
    }

    @Override
    protected void onTick(ServerTickEvent.Post event) {
        pushMeasurement(meter.getAndSet(0));
        super.onTick(event);
    }
}
