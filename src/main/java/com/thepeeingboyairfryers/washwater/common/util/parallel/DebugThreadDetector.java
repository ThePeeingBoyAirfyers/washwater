package com.thepeeingboyairfryers.washwater.common.util.parallel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class DebugThreadDetector {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugThreadDetector.class);
    private final Lock lock = new ReentrantLock();
    private StackTraceElement[] stackTrace;

    private DebugThreadDetector() {

    }

    public static DebugThreadDetector newInstance() {
        return new DebugThreadDetector();
    }

    public void acquire() {
        if (Thread.currentThread().getName().equals("Render thread")) return;
        if (!lock.tryLock()) {
            LOGGER.error("Trying to second lock\n{}", Arrays.stream(stackTrace).map(Object::toString).collect(Collectors.joining("\n\tat ")));
            lock.lock();
        } else {
            stackTrace = Thread.currentThread().getStackTrace();
        }
    }

    public void release() {
        if (Thread.currentThread().getName().equals("Render thread")) return;
        lock.unlock();
    }
}
