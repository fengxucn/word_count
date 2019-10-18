package com.rs.utils;

import java.util.*;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.*;

public class Executor extends Thread {
    int index = 0;
    String url = "";
    int taskSize = 0;
    long MAX = 0;
    public Executor(int i, String url, int n, long MAX){
        this.index = i;
        this.url = url;
        this.taskSize = n;
        this.MAX = MAX;
    }

    public void run()
    {
        try
        {
            int i = 0;
            Map<String, Long> result = new HashMap<String, Long>();
            Map<String, Long> temp;
            long max_size = MAX_SIZE_EACH_TASK();
            do{
                long off = taskSize * i * max_size;
                long start = off + index * max_size;
                long end = off + (index + 1) * max_size - 1;
                if(start > MAX)
                    break;
                System.out.println("Thread Index: " + index + " read from start: " + start + " to end: " + end);
                temp = Worker.rangeRead(url, start, end);

                for(String key : temp.keySet()){
                    if(!result.containsKey(key)){
                        result.put(key,(long)0);
                    }
                    result.put(key, result.get(key) + temp.get(key));
                }
                i++;
            }while (!temp.isEmpty());


            String fileName = MAP_RESULT + Thread.currentThread().getName() + "_" + index + ".txt";

            save(sort(result), fileName);

            done();

            System.out.println("Done");
        }
        catch (Exception e)
        {
            // Throwing an exception
            System.out.println ("Exception is caught");
        }
    }
}