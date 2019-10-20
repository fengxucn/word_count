package com.rs.utils;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class WorkerTest {
    @Before
    public void init(){
        Utils.init();
        Configs.loadProps(System.getProperty("user.dir") + "/src/main/config/config.properties");
    }

    @Test
    public void downloadTest_Success(){
        int length = new Random().nextInt(524287);
        byte[] data = Worker.download("http://www.gutenberg.org/files/2600/2600-0.txt", 0, length);
        assertEquals(data.length, length+1);
    }

    @Test
    public void downloadTest_Zero(){
        int size = Utils.getTotalSize("http://www.gutenberg.org/files/2600/2600-0.txt");
        byte[] data = Worker.download("http://www.gutenberg.org/files/2600/2600-0.txt", size +1, size + 10);
        assertEquals(data.length, 0);
    }

    @Test
    public void downloadTest_Failed(){
        byte[] data = Worker.download("http://www.gutenberg.org/files/2600/2600-0.txt", -10, -9);
        assertEquals(data.length, 0);
    }

    @Test
    public void downloadTest_WrongURL(){
        byte[] data = Worker.download("enberg.org/files/2600/2600-0.txt", -10, -9);
        assertEquals(data.length, 0);
    }

    @Test
    public void countTest_Success(){
        Map<String, Long> result = Worker.count( new ByteArrayInputStream(Worker.download("http://www.gutenberg.org/files/2600/2600-0.txt", 0, 524287)));
        assertEquals(result.size(), 7347);
        assertEquals(result.get("princess"), 185, 0.0);
    }

    @Test
    public void countTest_Empty(){
        Map<String, Long> result = Worker.count(new ByteArrayInputStream(new byte[0]));
        assertEquals(result.size(), 0);
    }
}
