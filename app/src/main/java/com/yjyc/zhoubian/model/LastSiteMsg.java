package com.yjyc.zhoubian.model;

public class LastSiteMsg {
    private long lastTime;
    private String lastBody;
    private int unReadNum;

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public String getLastBody() {
        return lastBody;
    }

    public void setLastBody(String lastBody) {
        this.lastBody = lastBody;
    }

    public int getUnReadNum() {
        return unReadNum;
    }

    public void setUnReadNum(int unReadNum) {
        this.unReadNum = unReadNum;
    }
}
