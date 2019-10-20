package com.rs.utils;

import com.rs.memory.DataPool;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Worker.dirInit;

public class Utils {
    static Logger logger = Logger.getLogger(Utils.class);

    public static List<Map.Entry<String, Long>> sort(Map<String, Long> map) {
        List<Map.Entry<String, Long>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, (a,b) -> b.getValue().compareTo(a.getValue()));
        return list;
    }

    public static void save(List<Map.Entry<String, Long>> data, String fileName) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

            long total_words = 0;
            for(Map.Entry<String, Long> kv : data){
                total_words += kv.getValue();
            }
            writer.write( "Total words : " + total_words + " (Remove stop words: " + needEscapeStopWords() + ")");
            writer.newLine();
            writer.newLine();
            int topN = getTopN();
            writer.write( "-----------Top " + topN + " words-----------");
            writer.newLine();
            int index = 0;
            for (;index<topN && index<data.size();index++) {
                Map.Entry<String, Long> kv = data.get(index);
                writer.write(kv.getKey() + "," + kv.getValue());
                writer.newLine();
            }
            writer.write( "-----------Other words-----------");
            writer.newLine();
            for (;index<data.size();index++) {
                Map.Entry<String, Long> kv = data.get(index);
                writer.write(kv.getKey() + "," + kv.getValue());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    public static void init() {
        dirInit(FINAL_RESULT);
    }

    public static Map<String, Long> reduce() {
        int executors = getExecutorNumber();
        while (!isAllDone(executors)) {
            try {
                Thread.sleep(new Random().nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Map<String, Long> count = new HashMap<>();
        for (List<Map.Entry<String, Long>> list : map_result) {
            for (Map.Entry<String, Long> kv : list) {
                String key = kv.getKey();
                Long value = kv.getValue();
                count.put(key, count.getOrDefault(key, (long) 0) + value);
            }
        }
        return count;
    }

    public static int getTotalSize(String path) {
        int size = 0;
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Range", "bytes=" + 0 + "-" + Long.MAX_VALUE);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            logger.info("Respnse Code: " + responseCode);
            size = urlConnection.getContentLength();
            logger.info("Content-Length: " + size);

            if (responseCode != HttpURLConnection.HTTP_PARTIAL) {
                logger.error("No file to download. Server replied HTTP code: " + responseCode);
                size = 0;
            }
            urlConnection.disconnect();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
            logger.error(mue);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error(ioe);
        }
        return size;
    }


    public static void map() {
        DataPool pool = new DataPool();

        Locks locks = new Locks();
        Producer producer = new Producer(getUrls(), pool, locks);
        logger.info("Start Producer: " + producer.getName());
        producer.start();

        int executors = getExecutorNumber();

        for (int i = 0; i < executors; i++) {
            Consumer consumer = new Consumer(pool, locks);
            logger.info("Start Consumer: " + consumer.getName());
            consumer.start();
        }
    }

    private static CopyOnWriteArrayList<List<Map.Entry<String, Long>>> map_result = new CopyOnWriteArrayList<>();

    public static void done(List<Map.Entry<String, Long>> result) {
        map_result.add(result);
    }

    private static boolean isAllDone(int n) {
        return map_result.size() == n;
    }

    public static List<String> getWords(String line) {
        List<String> result = new ArrayList<String>();
        Pattern p = Pattern.compile("[\\p{L}']+");
        Matcher m = p.matcher(line);

        while (m.find()) {
            String word = line.substring(m.start(), m.end());
            if (!needEscapeStopWords() || !isStopWords(word))
                result.add(word);
        }
        return result;
    }

    private static Set<String> stopWords = null;

    private static boolean isStopWords(String word) {
        if (stopWords == null)
            stopWords = getStopWords();
        if (word.length() <= 1)
            return true;
        return stopWords.contains(word.toLowerCase());
    }
}
