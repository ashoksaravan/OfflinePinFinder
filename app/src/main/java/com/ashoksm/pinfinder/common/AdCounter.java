package com.ashoksm.pinfinder.common;


public class AdCounter {

    private static AdCounter ourInstance = new AdCounter();
    private static int count = 0;

    public synchronized static AdCounter getInstance() {
        return ourInstance;
    }

    private AdCounter() {
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized void incrementCount() {
        count++;
    }
}
