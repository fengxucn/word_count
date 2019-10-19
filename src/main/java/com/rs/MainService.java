package com.rs;

import com.rs.utils.Executor;
import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.*;

public class MainService {

    public static void main(String[] args) {
        run(args);
    }

    private static void run1(String[] args) {
        loadProps(args[0]);

        init();
        for (String path : getUrls()) {
            byte[] all_data = getInputStream(path, 0, Long.MAX_VALUE);
            int size = all_data.length;
            int size_each_executor = MAX_SIZE_EACH_TASK();
            int n = 0;
            for (int i = 0; ; i++) {
                int start = i * size_each_executor;
                if (start > size)
                    break;
                byte[] data = Arrays.copyOfRange(all_data, start, (start + size_each_executor));
                Executor object = new Executor(i, data);
                object.start();
                n++;
            }

            while (!allDone(n)) {
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
