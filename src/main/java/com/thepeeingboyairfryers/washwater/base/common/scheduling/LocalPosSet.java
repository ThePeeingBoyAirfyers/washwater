package com.thepeeingboyairfryers.washwater.base.common.scheduling;

public interface LocalPosSet {

    void forEach(LocalPosConsumer consumer);

    void add(int x, int y, int z);
    void remove(int x, int y, int z);

    boolean isEmpty();

    void clear();

    interface LocalPosConsumer {
        void accept(int x, int y, int z);
    }
}
