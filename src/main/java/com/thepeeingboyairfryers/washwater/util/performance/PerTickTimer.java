package com.thepeeingboyairfryers.washwater.util.performance;

public class PerTickTimer extends PerTickMeasurements {
    public PerTickTimer(String name, int iMaxMeasurements) {
        super(name, iMaxMeasurements, l -> l / 1000000 + "."  + (l / 100000) % 10 + "ms");
    }

    public Context push() {
        return new Context(System.nanoTime(), getRootName());
    }

    public class Context implements AutoCloseable {
        private final long startTime;
        private final String name;

        public Context(long iStartTime, String iName) {
            this.startTime = iStartTime;
            this.name = iName;
        }

        public Context push(String iName) {
            return new Context(System.nanoTime(), name + "|" + iName);
        }

        @Override
        public void close() {
            pushMeasurement(System.nanoTime() - startTime, name);
        }
    }
}
