package com.rs.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import static com.rs.utils.Configs.*;
import static com.rs.utils.Utils.getWords;

public class Worker {
    public static void dirInit(String dir) {
        try {
            File directory = new File(dir);
            if (directory.exists()) {
                FileUtils.deleteDirectory(new File(dir));
            }
            directory.mkdir();
        } catch (IOException e) {
            //TODO
        }
    }

    public static byte[] download(String path, long start, long end) {
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            System.out.println("start: " + start + " end: " + end);
            System.out.println("Respnse Code: " + responseCode);
            int size = urlConnection.getContentLength();
            System.out.println("Content-Length: " + size);
            byte[] stream = new byte[0];
            //The HTTP 206 Partial Content success status response code indicates that the request has succeeded
            // and has the body contains the requested ranges of data
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                stream = IOUtils.toByteArray(urlConnection);
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
            }
            urlConnection.disconnect();
            return stream;

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return new byte[0];
    }

    public static Map<String, Long> count(byte[] stream) {
        Map<String, Long> result = new HashMap<>();

        try {
            InputStream inputStream = new ByteArrayInputStream(stream);
            ;
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            while (reader.ready()) {
                String line = reader.readLine();
                List<String> words = getWords(line);

                for (String word : words) {
                    String key = word.toLowerCase();
                    result.put(key, result.getOrDefault(key, (long)0) + 1);
                }
            }
            inputStream.close();
            reader.close();
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return result;
    }
}
