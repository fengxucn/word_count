package com.rs.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {
    @Before
    public void init(){
        Utils.init();
        Configs.loadProps(System.getProperty("user.dir") + "/src/main/config/config.properties");
    }

    @Test
    public void getTotalSizeTest_Success(){
        int size = Utils.getTotalSize("http://www.gutenberg.org/files/2600/2600-0.txt");
        assertEquals(size, 3359549);
    }
}
