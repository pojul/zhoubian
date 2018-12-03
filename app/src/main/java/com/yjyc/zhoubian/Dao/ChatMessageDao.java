package com.yjyc.zhoubian.Dao;

import android.database.Cursor;

import com.yjyc.zhoubian.Dao.Util.DaoUtil;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.util.List;

public class ChatMessageDao {

    public void insertMessage(ChatMessage message){
        DaoUtil.executeUpdate(generInsertSql(message));
    }

    public String generInsertSql(ChatMessage message){
        int status = 0;
        if(message.getMessage().getMsgStatus() == ECMessage.MessageStatus.FAILED){
            status = -1;
        }
        String sql = "insert into chat_message(from_id, to_id" +
                ", type, time_milli, msg_status, text, thumb_url, hd_url, local_url) values(" +
                "'" + message.getMessage().getForm() + "'," +
                "'" + message.getMessage().getTo() + "'," +
                "'" + message.getMessageType() + "'," +
                "" + message.getMessage().getMsgTime() + "," +
                "'" + status + "',";
        switch (message.getMessageType()){
            case ChatMessage.MESS_TYPE_TEXT:
                ECTextMessageBody textMessageBody = (ECTextMessageBody) message.getMessage().getBody();
                sql = sql +
                        "'" + textMessageBody.getMessage() + "'," +
                        "''," +
                        "''," +
                        "''" +
                        ")";
                break;
            case ChatMessage.MESS_TYPE_PIC:
                ECImageMessageBody picMessageBody = (ECImageMessageBody) message.getMessage().getBody();
                sql = sql +
                        "''," +
                        "'" + picMessageBody.getThumbnailFileUrl() + "'," +
                        "'" + picMessageBody.getHDImageURL() + "'," +
                        "'" + picMessageBody.getLocalUrl() + "'" +
                        ")";
                break;
        }
        return sql;
    }

    public List<ECMessage> getHistoryMessage(String from, String to, long time, int num){
        String sql = "select * from chat_message where ((from_id = " + from + " and to_id = " + to + ") or " +
                "(from_id = " + to + " and to_id = " + from + ")) and time_milli < " + time +" order by id desc limit " + num +" offset 0";
        //String sql = "select * from chat_message";
        return DaoUtil.executeQueryMessage(sql);
    }

    public static ECMessage CreateMessage(Cursor cursor){
        try{
            int type = cursor.getInt(cursor.getColumnIndexOrThrow("type"));
            switch (type){
                case ChatMessage.MESS_TYPE_TEXT:
                    return CreateTextECMessage(cursor);
                case ChatMessage.MESS_TYPE_PIC:
                    return CreatePicECMessage(cursor);
            }
        }catch(Exception e){
            LogUtil.e(e.getMessage());
            return null;
        }
        return null;
    }

    public static ECMessage CreatePicECMessage(Cursor cursor){
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
        msg = CreateECMessage(cursor, msg);
        ECImageMessageBody msgBody = new ECImageMessageBody();
        msgBody.setLocalUrl(cursor.getString(cursor.getColumnIndexOrThrow("local_url")));
        msgBody.setHDImageURL(cursor.getString(cursor.getColumnIndexOrThrow("hd_url")));
        msgBody.setThumbnailFileUrl(cursor.getString(cursor.getColumnIndexOrThrow("thumb_url")));
        msg.setBody(msgBody);
        return msg;
    }

    public static ECMessage CreateTextECMessage(Cursor cursor){
        ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
        msg = CreateECMessage(cursor, msg);
        ECTextMessageBody msgBody = new ECTextMessageBody(cursor.getString(cursor.getColumnIndexOrThrow("text")));
        msg.setBody(msgBody);
        return msg;
    }

    public static ECMessage CreateECMessage(Cursor cursor, ECMessage msg){
        msg.setFrom(cursor.getString(cursor.getColumnIndexOrThrow("from_id")));
        msg.setTo(cursor.getString(cursor.getColumnIndexOrThrow("to_id")));
        msg.setMsgTime(cursor.getLong(cursor.getColumnIndexOrThrow("time_milli")));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow("msg_status"));
        if(status == -1){
            msg.setMsgStatus(ECMessage.MessageStatus.FAILED);
        }else{
            msg.setMsgStatus(ECMessage.MessageStatus.SUCCESS);
        }
        return msg;
    }

}
