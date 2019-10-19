package com.rs.utils;

import java.util.Map;
import java.util.UUID;

import static com.rs.utils.Configs.MAP_RESULT;
import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.count;

public class Executor extends Thread {
    int index = 0;
    byte[] stream;

    public Executor(int i, byte[] stream) {
        this.index = i;
        this.stream = stream;
    }

    public void run() {

        Map<String, Long> result = count(stream);

        String fileName = MAP_RESULT + Thread.currentThread().getName() + "_" + index + "_" + UUID.randomUUID() + ".txt";

        save(sort(result), fileName);

        done();

        System.out.println("Done");
    }
}
