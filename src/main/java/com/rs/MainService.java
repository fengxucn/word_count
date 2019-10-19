package com.rs;

import com.rs.memory.DataPool;
import com.rs.utils.Consumer;
import com.rs.utils.Executor;
import com.rs.utils.Producer;

import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.*;

public class MainService {

    public static void main(String[] args) {
        run1(args);
    }

    private static void run1(String[] args) {
        loadProps(args[0]);

        init();

        DataPool pool = new DataPool();
        Producer producer = new Producer(getUrls(), pool);
        System.out.println("Start Producer: " + producer.getName());
        producer.start();

        int executors = getExecutorNumber();

        for (int i = 0; i < executors; i++) {
            Consumer consumer = new Consumer(pool);
            System.out.println("Start Consumer: " + consumer.getName());
            consumer.start();
        }

        while (!allDone(executors)) {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Map.Entry<String, Long>> result = sort(reduce());

        save(result, FINAL_RESULT + "word_count_all.txt");

        save(result.subList(0, getTopN()), FINAL_RESULT + "word_count_top" + getTopN() + ".txt");
    }

    private static void run(String[] args) {
        loadProps(args[0]);

        init();

        for (String url : getUrls())
            map(url);

        List<Map.Entry<String, Long>> result = sort(reduce());

        save(result, FINAL_RESULT + "word_count_all.txt");

        save(result.subList(0, getTopN()), FINAL_RESULT + "word_count_top" + getTopN() + ".txt");
    }


}
