package com.rs.memory;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;

public class Cell {

    private byte[] data;
    private int step;
    private int length;

    public Cell(byte[] data) {
        this.data = data;
        this.step = MAX_SIZE_EACH_TASK();
        this.length = data.length;
    }

    int index = 0;

    public boolean isEmpty() {
        return index >= length;
    }

    public ByteArrayInputStream read() {
        int start = 0;

        if (index > length)
            return new ByteArrayInputStream(new byte[0]);
        start = index;
        index += step;

        return new ByteArrayInputStream(data, start , step);
    }
}
