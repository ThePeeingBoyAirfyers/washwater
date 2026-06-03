package com.thepeeingboyairfryers.washwater.util;

public class SwapPair<T> {
    private T one;
    private T two;

    public SwapPair(T iOne, T iTwo) {
        this.one = iOne;
        this.two = iTwo;
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
