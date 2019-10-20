package com.rs.utils;

import com.rs.memory.DataPool;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.rs.utils.Configs.FINAL_RESULT;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.count;
/**
 * created by Feng Xu on Oct. 19th
 */
public class Consumer extends Thread {
    static Logger logger = Logger.getLogger(Consumer.class);
    DataPool pool;
    Locks locks;

    public Consumer(DataPool pool, Locks locks) {
        this.pool = pool;
        this.locks = locks;
    }

    public void run() {
        Map<String, Long> result = new HashMap<>();
        while (true) {
            ByteArrayInputStream data;
            locks.readLock.lock();
            try {
                while (pool.isEmpty() && !Producer.done.get()) {
                    try {
                        locks.hasData.await(new Random().nextInt(100), TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (Producer.done.get() && pool.isEmpty()) {
                    break;
                }
                data = pool.read();
                locks.needData.signalAll();
            }
            finally {
                locks.readLock.unlock();
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
