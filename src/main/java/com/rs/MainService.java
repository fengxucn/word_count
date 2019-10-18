package com.rs;

import com.rs.utils.Executor;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;
import static com.rs.utils.Worker.*;

public class MainService {

    public static void main(String[] args) {
//        loadProps(args[0]);
//        split("http://www.gutenberg.org/files/2600/2600-0.txt");
          run(args);
    }

    private static void run(String[] args){
        loadProps(args[0]);

        init();

        int sum = 0;
        for (String url : getUrls())
            sum += map(url);

        while (!allDone(sum)){
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<Map.Entry<String, Long> > result = sort(reduce());

        save(result,  FINAL_RESULT + "word_count_all.txt");

        save(result.subList(0,getTopN()), FINAL_RESULT + "word_count_top" + getTopN() + ".txt");
    }


}
