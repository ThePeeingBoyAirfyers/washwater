package com.thepeeingboyairfryers.washwater.util.performance;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class PerTickMeasurements {
    private final Map<String, Measurement> times = new HashMap<>();
    private final int maxMeasurements;
    private final Long2ObjectFunction<String> formatter;
    private final String rootName;

    public PerTickMeasurements(String name, int iMaxMeasurements, String suffix) {
        this(name, iMaxMeasurements, d -> d + suffix);
    }

    public PerTickMeasurements(String name, int iMaxMeasurements, Long2ObjectFunction<String> iFormatter) {
        this.maxMeasurements = iMaxMeasurements;
        this.formatter = iFormatter;
        rootName = name;

        NeoForge.EVENT_BUS.addListener(this::onTick);
    }

    protected void onTick(ServerTickEvent.Post event) {
        times.values().forEach(Measurement::onTick);
    }

    public void pushMeasurement(long time) {
        pushMeasurement(time, "");
    }

    public void pushMeasurement(long time, String name) {
        times.computeIfAbsent(name, n -> new Measurement(maxMeasurements, n.isEmpty() ? rootName : n, formatter)).push(time);
    }

    public Collection<Measurement> getData() {
        return times.values();
    }

    protected String getRootName() {
        return rootName;
    }

    public static class Measurement {
        private final long[] measurements;
        private final LongList inTick = new LongArrayList();
        private final String name;
        private final Long2ObjectFunction<String> formatter;
        private int idx;
        private int measurementsLength;

        private Measurement(int measurementSize, String iName, Long2ObjectFunction<String> iFormatter) {
            this.measurements = new long[measurementSize];
            this.name = iName;
            this.formatter = iFormatter;
        }

        private void push(long measurement) {
            synchronized (inTick) {
                inTick.add(measurement);
            }
        }

        public LongStream readLast(int amount) {
            if (amount > measurementsLength)
                amount = measurementsLength;

            int nIdx = idx - amount;
            IntStream indices;
            if (nIdx < 0) {
                indices = IntStream.concat(IntStream.range(0, idx),  IntStream.range(measurements.length + nIdx, measurements.length));
            } else {
                indices = IntStream.range(nIdx, idx);
            }

            return indices.mapToLong(i -> measurements[i]).filter(l -> l != Long.MAX_VALUE);
        }

        public long getPercentageWise(double percentage, int overTimeInTicks) {
            int lookLength = Math.min(overTimeInTicks, measurementsLength);
            long[] result = new long[(int) (lookLength * (1.0 - percentage)) + 1];
            var iter = readLast(lookLength).iterator();

            // TODO lookLength is wrong amount, when there are empty readings
            int lowestIndex = 0;
            while (iter.hasNext()) {
                long l = iter.nextLong();
                if (result[lowestIndex] < l) {
                    result[lowestIndex] = l;

                    for (int i = 0; i < result.length; i++) {
                        if (result[lowestIndex] > result[i]) {
                            lowestIndex = i;
                        }
                    }
                }
            }

            return result[lowestIndex];
        }

        public String getName() {
            return name;
        }

        public String format(long value) {
            return formatter.apply(value);
        }

        private void onTick() {
            synchronized (inTick) {
                long avg;
                if (inTick.isEmpty()) {
                    // We havent measured anything real yet so no need to start doing that
                    if (measurementsLength == 0) return;
                    avg = Long.MIN_VALUE;
                } else if (inTick.size() == 1) {
                    avg = inTick.getFirst();
                } else {
                    avg = (long) inTick.longStream().average().orElseThrow();
                }

                inTick.clear();

                if (measurementsLength < measurements.length)
                    measurementsLength++;

                measurements[idx] = avg;
                idx = (idx + 1) % measurements.length;
            }
        }
    }
}
