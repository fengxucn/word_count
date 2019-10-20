package com.rs.memory;

import java.util.LinkedList;
import java.util.List;

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

    public byte[] read(){
        if(isEmpty())
            return new byte[0];

        Cell cell = pool.get(0);
        byte[] data = cell.read();
        if(cell.isEmpty()){
            pool.remove(0);
        }

        return data;
    }

}
