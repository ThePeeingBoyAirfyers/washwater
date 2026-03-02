package com.thepeeingboyairfryers.washwater.common.util.parallel;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DebugReadWriteLock implements ReadWriteLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebugReadWriteLock.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = new DebugLock(lock.readLock());
    private final Lock writeLock = new DebugLock(lock.writeLock());

    private DebugReadWriteLock() {

    }

    public static ReadWriteLock newInstance() {
        if (Thread.currentThread().getName().equals("Render thread")) {
            return new ReentrantReadWriteLock();
        }

        return new DebugReadWriteLock();
    }


    @Override
    public @NotNull Lock readLock() {
        return readLock;
    }

    @Override
    public @NotNull Lock writeLock() {
        return writeLock;
    }

    private static class DebugLock implements Lock {
        private final Lock self;
        private StackTraceElement[] stackTrace;

        DebugLock(Lock iLock) {
            this.self = iLock;
        }

        @Override
        public void lock() {
            if (!self.tryLock()) {
                LOGGER.error("Trying to second lock\n{}", Arrays.stream(stackTrace).map(Object::toString).collect(Collectors.joining("\n\tat ")));
                self.lock();
            } else {
                stackTrace = Thread.currentThread().getStackTrace();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            self.lockInterruptibly();
        }

        @Override
        public boolean tryLock() {
            return self.tryLock();
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            return self.tryLock(time, unit);
        }

        @Override
        public void unlock() {
            self.unlock();
        }

        @Override
        public @NotNull Condition newCondition() {
            return self.newCondition();
        }
    }
}
