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

    //iterate through all the lines in the file
    // allowing for processing of each line without keeping references to them, so can used for big size(TB level) file
    public static int split(String path, long max_size) {

        FileInputStream inputStream = null;
        Scanner sc = null;
        int sum = 0;
        try {
            URL url = new URL(path);
            sc = new Scanner(url.openStream(), "UTF-8");
            long tmp_total = 0;
            String fileName =  sum + ".txt";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                tmp_total += line.length();
                writer.write(line);
                writer.newLine();
                if (tmp_total >= max_size) {
                    sum++;
                    tmp_total = 0;
                    writer.close();
                    fileName =  sum + ".txt";
                    writer = new BufferedWriter(new FileWriter(fileName));
                }
            }
            writer.close();
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException ex) {
            /*
            TODO exception process
             */
            ex.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (sc != null) {
                sc.close();
            }
        }

        return sum;
    }

    public static int split(String url) {
        return split(url, MAX_SIZE_EACH_TASK());
    }

    public static Map<String, Long> smallFileRead(int index) {
        Map<String, Long> result = new HashMap<String, Long>();
        BufferedReader reader;
        try {
            String fileName =  index + ".txt";
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                List<String> words = getWords(line);

                for (String word : words) {
                    String key = word.toLowerCase();
                    if (!result.containsKey(key)) {
                        result.put(key, (long) 0);
                    }
                    result.put(key, result.get(key) + 1);
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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
        Map<String, Long> result = new HashMap<String, Long>();

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

    public static synchronized Map<String, Long> rangeRead(String path, long start, long end) {
        Map<String, Long> result = new HashMap<>();
        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Range", "bytes=" + start + "-" + end);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            System.out.println("start: " + start + " end: " + end);
            System.out.println("Respnse Code: " + responseCode);
            System.out.println("Content-Length: " + urlConnection.getContentLengthLong());

            //The HTTP 206 Partial Content success status response code indicates that the request has succeeded
            // and has the body contains the requested ranges of data
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                InputStream inputStream = urlConnection.getInputStream();
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
            } else {
                System.out.println("No file to download. Server replied HTTP code: " + responseCode);
                return result;
            }

            urlConnection.disconnect();

        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


        return result;
    }

    public static void main(String[] args) {

        Map<String, Long> res = rangeRead("http://www.gutenberg.org/files/2600/2600-0.txt", 2048, 64 * 1024);

        System.out.println(res);
    }
}
