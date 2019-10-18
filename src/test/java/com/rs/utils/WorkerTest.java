package com.rs.utils;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class WorkerTest {

    @Test
    public void splitTest(){
        assertEquals(48, Worker.split("http://www.gutenberg.org/files/2600/2600-0.txt"), 0.0);
    }
}
