package com.rs.utils;

import java.util.concurrent.locks.*;

public class Locks {
//    public  ReadWriteLock rwlock = new ReentrantReadWriteLock();
    public Lock aLock = new ReentrantLock();
    public  Lock writeLock = aLock;
    public  Condition hasData = writeLock.newCondition();
    public  Lock readLock = aLock;
    public  Condition needData = readLock.newCondition();

}
