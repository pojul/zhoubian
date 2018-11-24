package com.yjyc.zhoubian.im.entity;

import com.yuntongxun.ecsdk.ECMessage;

public class ChatMessage {

    public static final int MESS_TYPE_TIME = -1;
    public static final int MESS_TYPE_TEXT = 0;
    public static final int MESS_TYPE_PIC = 1;
    public static final int MESS_TYPE_VOICE = 2;
    public static final int MESS_TYPE_FILE = 3;
    public static final int MESS_TYPE_LOCATION = 4;

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
        }else if(message.getType() == ECMessage.Type.IMAGE){
            return 1;
        }else if(message.getType() == ECMessage.Type.VOICE){
            return 2;
        }else if(message.getType() == ECMessage.Type.FILE){
            return 3;
        }else if(message.getType() == ECMessage.Type.LOCATION){
            return 4;
        }
        return -2;
    }

    public long getTime() {
        return time;
    }
}
