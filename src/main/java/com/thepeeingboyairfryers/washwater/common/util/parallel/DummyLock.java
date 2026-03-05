package com.thepeeingboyairfryers.washwater.common.util.parallel;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DummyLock implements Lock {
    @Override
    public void lock() {

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return true;
    }

    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return true;
    }

    @Override
    public void unlock() {

    }

    @Override
    public @NotNull Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public static final DummyLock INSTANCE = new DummyLock();
}
