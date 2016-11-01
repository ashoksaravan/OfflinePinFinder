package com.ashoksm.pinfinder.common;


public class AdCounter {

    private static AdCounter ourInstance = new AdCounter();
    private static int count = 0;

    public static AdCounter getInstance() {
        return ourInstance;
    }

    private AdCounter() {
    }

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
    }
}
