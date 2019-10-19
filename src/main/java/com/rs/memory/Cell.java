package com.rs.memory;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;

public class Cell {

    byte[] data;
    int step;
    int length;

    public Cell(byte[] data) {
        this.data = data;
        this.step = MAX_SIZE_EACH_TASK();;
        this.length = data.length;
    }

    int index = 0;

    public boolean isEmpty() {
        return index > length;
    }

    public byte[] read() {
        int start = 0;

        if (index > length)
            return new byte[0];
        start = index;
        index += step;

        return Arrays.copyOfRange(data, start, start + step);
    }
}
