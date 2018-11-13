package com.yjyc.zhoubian.im.entity;

import com.yuntongxun.ecsdk.ECMessage;

public class ChatMessage {

    public static final int MESS_TYPE_TIME = -1;
    public static final int MESS_TYPE_TEXT = 0;

    private ECMessage message;
    private int messageType;
    private long time;

    public ECMessage getMessage() {
        return message;
    }

    public ChatMessage(ECMessage message) {
        this.message = message;
    }

    public ChatMessage(long time) {
        this.time = time;
    }

    public void setMessage(ECMessage message) {
        this.message = message;
    }

    public int getMessageType() {
        if(message == null){
            return -1;
        }
        if(message.getType() == ECMessage.Type.TXT){
            return 0;
        }
        return 2;
    }

    public long getTime() {
        return time;
    }
}
