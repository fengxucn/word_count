package com.rs.memory;

import com.rs.utils.Configs;
import com.rs.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;
import static org.junit.Assert.assertEquals;

public class DataPoolTest {
    @Before
    public void init(){
        Utils.init();
        Configs.loadProps(System.getProperty("user.dir") + "/src/main/config/config.properties");
    }
    @Test
    public void readTest(){
        DataPool pool = new DataPool();

        assertEquals(pool.isEmpty(), true);
        assertEquals(pool.isFull(), false);

        pool.add(new Cell(new byte[1]));
        pool.add(new Cell(new byte[1]));
        pool.add(new Cell(new byte[1]));
        assertEquals(pool.isEmpty(), false);


        pool.read();
        assertEquals(pool.isEmpty(), false);
        pool.read();
        assertEquals(pool.isEmpty(), false);
        pool.read();
        assertEquals(pool.isEmpty(), true);

        int step = MAX_SIZE_EACH_TASK();

        pool.add(new Cell(new byte[step*2]));
        pool.add(new Cell(new byte[step*2]));

        pool.read();
        assertEquals(pool.isEmpty(), false);
        pool.read();
        assertEquals(pool.isEmpty(), false);
        pool.read();
        assertEquals(pool.isEmpty(), false);
        pool.read();
        assertEquals(pool.isEmpty(), true);

        assertEquals(pool.read().length, 0);
    }
}
