package com.rs.utils;

import com.rs.memory.DataPool;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.rs.utils.Configs.MAP_RESULT;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.count;

public class Consumer extends Thread {

    DataPool pool;

    public Consumer(DataPool pool) {
        this.pool = pool;
    }

    public void run() {
        while (true) {
            byte[] data;
            synchronized (pool) {
                while (pool.isEmpty() && !Producer.done.get()) {
                    try {
                        pool.wait(new Random().nextInt(100));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (Producer.done.get() && pool.isEmpty()) {
                    break;
                }
                data = pool.read();
                pool.notifyAll();
            }
            Map<String, Long> result = count(data);

            String fileName = MAP_RESULT + Thread.currentThread().getName() + "_" + UUID.randomUUID() + ".txt";

            save(sort(result), fileName);
        }

        done();

        System.out.println("Thread: " + Thread.currentThread().getName() +" Done!");
    }

}
