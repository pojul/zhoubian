package com.yjyc.zhoubian.im.entity;

import com.yjyc.zhoubian.model.UserInfo;

public class Conversation {

    private UserInfo friend;
    private String from;
    private int unReadMessage;
    private String lastMessage;
    private String lastChatTime;
    private boolean isNotTroubled;
    private long lastTimeMilli;

    public long getLastTimeMilli() {
        return lastTimeMilli;
    }

    public void setLastTimeMilli(long lastTimeMilli) {
        this.lastTimeMilli = lastTimeMilli;
    }

    public UserInfo getFriend() {
        return friend;
    }

    public void setFriend(UserInfo friend) {
        this.friend = friend;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getUnReadMessage() {
        return unReadMessage;
    }

    public void setUnReadMessage(int unReadMessage) {
        this.unReadMessage = unReadMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastChatTime() {
        return lastChatTime;
    }

    public void setLastChatTime(String lastChatTime) {
        this.lastChatTime = lastChatTime;
    }

    public boolean isNotTroubled() {
        return isNotTroubled;
    }

    public void setNotTroubled(boolean notTroubled) {
        isNotTroubled = notTroubled;
    }
}
