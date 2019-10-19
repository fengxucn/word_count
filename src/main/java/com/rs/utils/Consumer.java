package com.rs.utils;

import com.rs.memory.DataPool;
import org.apache.log4j.Logger;

import java.util.*;

import static com.rs.utils.Configs.FINAL_RESULT;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.count;

public class Consumer extends Thread {
    static Logger logger = Logger.getLogger(Consumer.class);
    DataPool pool;

    public Consumer(DataPool pool) {
        this.pool = pool;
    }

    public void run() {
        Map<String, Long> result = new HashMap<>();
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
            Map<String, Long> tmp = count(data);

            for(String key : tmp.keySet()){
                result.put(key, result.getOrDefault(key, (long)0) + tmp.get(key));
            }
        }

        String fileName = FINAL_RESULT + "Part Result for: " + Thread.currentThread().getName() + ".txt";

        List<Map.Entry<String, Long>> sorted_result = sort(result);

        save(sorted_result, fileName);

        done(sorted_result);

        logger.info("Thread: " + Thread.currentThread().getName() +" Done!");
    }

}
