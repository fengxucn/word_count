package com.rs.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.apache.log4j.Logger;
import static com.rs.utils.Utils.getWords;
/**
 * created by Feng Xu on Oct. 19th
 */
public class Worker {
    static Logger logger = Logger.getLogger(Worker.class);
    public static void dirInit(String dir) {
        try {
            File directory = new File(dir);
            if (directory.exists()) {
                FileUtils.deleteDirectory(new File(dir));
            }
            directory.mkdir();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static byte[] download(String path, long start, long end) {
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            logger.info("start: " + start + " end: " + end);
            logger.info("Respnse Code: " + responseCode);
            int size = urlConnection.getContentLength();
            logger.info("Content-Length: " + size);
            byte[] stream = new byte[0];
            //The HTTP 206 Partial Content success status response code indicates that the request has succeeded
            // and has the body contains the requested ranges of data
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                stream = IOUtils.toByteArray(urlConnection);
            } else {
                logger.error(path + " not support range download. Server replied HTTP code: " + responseCode);
            }
            urlConnection.disconnect();
            return stream;

        } catch (MalformedURLException mue) {
            logger.error(mue);
        } catch (IOException ioe) {
            logger.error(ioe);
        }
        return new byte[0];
    }

    public static Map<String, Long> count(ByteArrayInputStream stream) {
        Map<String, Long> result = new HashMap<>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            while (reader.ready()) {
                String line = reader.readLine();
                List<String> words = getWords(line);

                for (String word : words) {
                    String key = word.toLowerCase();
                    result.put(key, result.getOrDefault(key, (long)0) + 1);
                }
            }
            reader.close();
        } catch (MalformedURLException mue) {
            logger.error(mue);
        } catch (IOException ioe) {
            logger.error(ioe);
        }
        return result;
    }
}
