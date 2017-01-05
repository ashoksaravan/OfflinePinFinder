package com.ashoksm.pinfinder.common;


public class AdCounter {

    private static AdCounter ourInstance = new AdCounter();
    private int count = 0;
    private boolean showAd;

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

    public boolean isShowAd() {
        return showAd;
    }

    public void setShowAd(boolean showAd) {
        this.showAd = showAd;
    }
}
