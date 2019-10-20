package com.rs.memory;

import com.rs.utils.Configs;
import com.rs.utils.Utils;
import org.junit.Before;
import org.junit.Test;

import static com.rs.utils.Configs.MAX_SIZE_EACH_TASK;
import static org.junit.Assert.assertEquals;

public class CellTest {
    @Before
    public void init(){
        Utils.init();
        Configs.loadProps(System.getProperty("user.dir") + "/src/main/config/config.properties");
    }
    @Test
    public void isEmptyTest(){
        Cell cell = new Cell(new byte[0]);
        assertEquals(cell.isEmpty(), true);

        cell = new Cell(new byte[1]);
        assertEquals(cell.isEmpty(), false);

        int step = MAX_SIZE_EACH_TASK();

        cell = new Cell(new byte[step]);
        cell.read();
        assertEquals(cell.isEmpty(), true);

        cell = new Cell(new byte[step+1]);
        cell.read();
        assertEquals(cell.isEmpty(), false);
    }

    @Test
    public void readTest(){
        int step = MAX_SIZE_EACH_TASK();

        Cell cell = new Cell(new byte[step*3]);
        cell.read();
        assertEquals(cell.isEmpty(), false);

        cell.read();
        assertEquals(cell.isEmpty(), false);

        cell.read();
        assertEquals(cell.isEmpty(), true);
    }
}
