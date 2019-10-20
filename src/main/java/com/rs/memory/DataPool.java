package com.rs.memory;

import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * created by Feng Xu on Oct. 19th
 */

public class DataPool {
    private List<Cell> pool = new LinkedList<>();

    private final int size_limit = 3;

    public int size(){
        return pool.size();
    }

    public void add(Cell data){
        pool.add(data);
    }

    public boolean isFull(){
        return pool.size() >= size_limit;
    }

    public boolean isEmpty(){
        return  pool.isEmpty();
    }

    public ByteArrayInputStream read(){
        if(isEmpty())
            return new ByteArrayInputStream(new byte[0]);

        Cell cell = pool.get(0);
        ByteArrayInputStream data = cell.read();
        if(cell.isEmpty()){
            pool.remove(0);
        }

        return data;
    }

}
