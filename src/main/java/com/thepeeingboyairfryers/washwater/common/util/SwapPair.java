package com.thepeeingboyairfryers.washwater.common.util;

public class SwapPair<T> {
    private T one;
    private T two;

    public SwapPair(T one, T two) {
        this.one = one;
        this.two = two;
    }

    public T getCurrent() {
        return one;
    }

    public T getOther() {
        return two;
    }

    public T swap() {
        T tmp = one;
        one = two;
        two = tmp;
        return one;
    }
}
