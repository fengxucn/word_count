package com.rs.utils;

import java.util.concurrent.locks.*;
/**
 * created by Feng Xu on Oct. 19th
 */
public class Locks {
    public Lock aLock = new ReentrantLock();
    public  Lock writeLock = aLock;
    public  Condition hasData = writeLock.newCondition();
    public  Lock readLock = aLock;
    public  Condition needData = readLock.newCondition();

}
