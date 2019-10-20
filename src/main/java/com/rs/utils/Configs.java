package com.rs.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Configs {
    public static final String FINAL_RESULT = "./result/";

    private static Properties prop = null;
    public static void loadProps(String path){
        if(prop == null) {
            prop = new Properties();
            try {
                InputStream input = new FileInputStream(path);
                // load a properties file
                prop.load(input);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static int MAX_SIZE_EACH_TASK(){
        String size = prop.getProperty("max_size_each_task", "64k");
        int value = Integer.parseInt(size.substring(0, size.length()-1));
        if(size.endsWith("k") || size.endsWith("K")){
            return value * 1024;
        }
        else if(size.endsWith("m") || size.endsWith("M")){
            return value * 1024 *1024;
        }
        else if(size.endsWith("g") || size.endsWith("G")){
            return value * 1024 * 1024 * 1024;
        }else{
            return value * 1024;
        }
    }

    public static Set<String> getStopWords(){
        Set<String> sws = new HashSet<>();
        for(String word : prop.getProperty("stop_words").split(",")){
            sws.add(word.trim());
        }
        return  sws;
    }

    public static boolean needEscapeStopWords(){
        return Boolean.parseBoolean(prop.getProperty("escape_stop_words", "true"));
    }

    public static Set<String> getUrls(){
        return new HashSet<>(Arrays.asList(prop.getProperty("urls").split(",")));
    }

    public static int getTopN(){
        return Integer.parseInt(prop.getProperty("topN", "5"));
    }

    public static int getExecutorNumber(){
        return Math.min(Integer.parseInt(prop.getProperty("executor_number", "5")), 10);
    }
}
