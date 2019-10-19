package com.rs.utils;

import com.rs.memory.Cell;
import com.rs.memory.DataPool;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;
import static com.rs.utils.Configs.getExecutorNumber;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.download;

public class Producer extends Thread {
    static Logger logger = Logger.getLogger(Producer.class);

    Set<String> urls;
    DataPool pool;
    public static AtomicBoolean done = new AtomicBoolean(false);
    public Producer(Set<String> urls, DataPool pool){
        this.urls = urls;
        this.pool = pool;
    }
    public void run() {

        int size_each_task = MAX_SIZE_EACH_TASK();
        int executors = getExecutorNumber();
        int step = size_each_task * executors;

        for(String url : urls){
            int total_size = getTotalSize(url);
            long begin = 0;
            do {
                long end = begin + step;
                byte[] data = download(url, begin, end-1);
                Cell cell = new Cell(data);
                synchronized (pool){
                    while (pool.isFull()) {
                        try {
                            pool.wait(new Random().nextInt(100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    pool.add(cell);
                    pool.notifyAll();
                }
                begin = end;
            } while (begin <= total_size);
        }

        done.set(true);
    }
}
