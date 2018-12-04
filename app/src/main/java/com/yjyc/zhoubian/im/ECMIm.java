package com.yjyc.zhoubian.im;

import android.content.Context;
import android.os.Vibrator;
import android.view.View;

import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.Dao.ChatMessageDao;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.im.chat.ui.ChatActivity;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.im.entity.Conversation;
import com.yjyc.zhoubian.model.LastSiteMsg;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.utils.DateUtil;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ECMIm {

    private List<IReceiveMessage> IReceiveMessages = new ArrayList<>();
    public HashMap<String, List<ChatMessage>> messages = new HashMap<>();
    public List<Conversation> conversations;
    private Context mContext;
    private static ECMIm ECMIm;
    private ChatActivity chatActivity;
    public MainActivitys mainActivitys;

    private ECMIm(Context context) {
        this.mContext = context;
        conversations = Hawk.get("conversations");
        if (conversations == null) {
            conversations = new ArrayList<>();
            Conversation conversation = new Conversation();
            conversation.setFrom("-1");
            conversation.setLastTimeMilli(System.currentTimeMillis());
            conversation.setLastChatTime(DateUtil.converterDate(System.currentTimeMillis()));
            conversation.setLastMessage("");
            conversations.add(conversation);
            synchronized (conversations) {
                Hawk.put("conversations", conversations);
            }
        }
    }

    public static void Instance(Context context) {
        if (ECMIm == null) {
            synchronized (ECMIm.class) {
                if (ECMIm == null) {
                    ECMIm = new ECMIm(context);
                }
            }
        }
    }

    public static ECMIm getInstance() {
        return ECMIm;
    }

    public void onReceiveMessage(ECMessage ecMessage) {
        if(ecMessage.getType() != ECMessage.Type.LOCATION && ecMessage.getType() != ECMessage.Type.TXT && ecMessage.getType() != ECMessage.Type.IMAGE
                && ecMessage.getType() != ECMessage.Type.VOICE && ecMessage.getType() != ECMessage.Type.FILE){
            return;
        }
        List<ChatMessage> chatRoomMessages = messages.get(ecMessage.getForm());
        if(chatRoomMessages == null){
            chatRoomMessages = new ArrayList<>();
            messages.put(ecMessage.getForm(), chatRoomMessages);
        }
        if(ecMessage.getType() == ECMessage.Type.IMAGE){
            ECImageMessageBody picMessageBody = (ECImageMessageBody) ecMessage.getBody();
            picMessageBody.setHDImageURL(picMessageBody.getRemoteUrl());
        }
        new ChatMessageDao().insertMessage(new ChatMessage(ecMessage));
        if(chatActivity != null && chatActivity.messageListAdapter != null
                && ecMessage.getForm().equals(("" + chatActivity.friendId))){
            chatActivity.messageListAdapter.addMessage(ecMessage);
        }else{
            addMessage(chatRoomMessages, ecMessage);
            Vibrator vibrator = (Vibrator)mContext.getSystemService(mContext.VIBRATOR_SERVICE);
            vibrator.vibrate(new long[]{100, 200, 100, 200}, -1);
        }
        checkNewConversation(ecMessage);
        updateHomeUnread();
    }

    public void addMessage(List<ChatMessage> messages, ECMessage ecMessage){
        ChatMessage chatMessage = new ChatMessage(ecMessage);
        synchronized (messages){
            if(messages.size() == 0 || ( ecMessage.getMsgTime() - messages.get((messages.size() - 1)).getMessage().getMsgTime() ) > 10 * 60 * 1000 ){
                messages.add(new ChatMessage(ecMessage.getMsgTime()));
                messages.add(chatMessage);
                return;
            }
            messages.add(chatMessage);
        }
    }

    public List<ChatMessage> getChatRoomMessages(String from){
        List<ChatMessage> chatRoomMessages = messages.get(from);
        if(chatRoomMessages == null){
            chatRoomMessages = new ArrayList<>();
            messages.put(from, chatRoomMessages);
        }
        return chatRoomMessages;
    }

    public void checkNewConversation(ECMessage ecMessage){
        Login login = Hawk.get("LoginModel");
        UserInfo owner = Hawk.get("userInfo");
        if(login == null || owner == null){
            return;
        }
        String from  = ecMessage.getForm();
        if(from.equals((login.uid + ""))){
            from = ecMessage.getTo();
        }
        Conversation conversation = getConversation(from);
        if(conversation == null){
            conversation = new Conversation();
            conversation.setFrom(from);
            ECMIm.getInstance().conversations.add(conversation);
            Hawk.put("conversations", ECMIm.getInstance().conversations);
            reqUserInfo(from, ecMessage, conversation);
        }else{
            updateConversation(conversation, ecMessage);
        }
    }

    public void clearConversationUndead(String from){
        synchronized (conversations){
            for (int i = 0; i < conversations.size(); i++) {
                Conversation conversation = conversations.get(i);
                if(conversation.getFrom().equals(from)){
                    conversation.setUnReadMessage(0);
                    if(mainActivitys != null && mainActivitys.conversationFragment!= null && mainActivitys.conversationFragment.conversationAdapter != null){
                        mainActivitys.conversationFragment.conversationAdapter.notifyDataSetChanged();
                    }
                    Hawk.put("conversations", conversations);
                }
            }
        }
        updateHomeUnread();
    }

    public void clearOfficialUndead(){
        synchronized (conversations){
            LastSiteMsg lastSiteMsg = Hawk.get("LastSiteMsg");
            if(lastSiteMsg != null){
                lastSiteMsg.setUnReadNum(0);
                Hawk.put("LastSiteMsg", lastSiteMsg);
                if(mainActivitys != null && mainActivitys.conversationFragment!= null && mainActivitys.conversationFragment.conversationAdapter != null){
                    mainActivitys.conversationFragment.conversationAdapter.notifyOfficialMsg();
                }
            }
        }
        updateHomeUnread();
    }

    private void updateConversation(Conversation conversation, ECMessage ecMessage) {
        String time = DateUtil.converterDate(ecMessage.getMsgTime());
        conversation.setLastChatTime(time.substring(0, time.lastIndexOf(" ")));
        if(ecMessage.getType() == ECMessage.Type.TXT){
            ECTextMessageBody textMessageBody = (ECTextMessageBody) ecMessage.getBody();
            conversation.setLastMessage(textMessageBody.getMessage());
        }else if(ecMessage.getType() == ECMessage.Type.IMAGE){
            conversation.setLastMessage("图片");
        }else if(ecMessage.getType() == ECMessage.Type.VOICE){
            conversation.setLastMessage("语音");
        }else if(ecMessage.getType() == ECMessage.Type.FILE){
            conversation.setLastMessage("文件");
        }else if(ecMessage.getType() == ECMessage.Type.LOCATION){
            conversation.setLastMessage("位置");
        }else{
            conversation.setLastMessage("");
        }
        if(chatActivity != null && chatActivity.messageListAdapter != null
                && conversation.getFrom().equals(("" + chatActivity.friendId))){
            conversation.setUnReadMessage(0);
        }else{
            conversation.setUnReadMessage(conversation.getUnReadMessage() + 1);
        }
        conversation.setLastTimeMilli(ecMessage.getMsgTime());
        if(mainActivitys != null && mainActivitys.conversationFragment!= null && mainActivitys.conversationFragment.conversationAdapter != null){
            mainActivitys.conversationFragment.conversationAdapter.notifyDataSetChanged();
        }
        Hawk.put("conversations", conversations);
    }

    public void createConversation(UserInfo friend, String from, ECMessage ecMessage) {
        Conversation conversation = new Conversation();
        String time = DateUtil.converterDate(ecMessage.getMsgTime());
        conversation.setLastChatTime(time.substring(0, time.lastIndexOf(" ")));
        if(ecMessage.getType() == ECMessage.Type.TXT){
            ECTextMessageBody textMessageBody = (ECTextMessageBody) ecMessage.getBody();
            conversation.setLastMessage(textMessageBody.getMessage());
        }else if(ecMessage.getType() == ECMessage.Type.IMAGE){
            conversation.setLastMessage("图片");
        }else if(ecMessage.getType() == ECMessage.Type.VOICE){
            conversation.setLastMessage("语音");
        }else if(ecMessage.getType() == ECMessage.Type.FILE){
            conversation.setLastMessage("文件");
        }else if(ecMessage.getType() == ECMessage.Type.LOCATION){
            conversation.setLastMessage("位置");
        }else{
            conversation.setLastMessage("");
        }
        conversation.setFriend(friend);
        conversation.setFrom(from);
        if(chatActivity != null && chatActivity.messageListAdapter != null
                && conversation.getFrom().equals(("" + chatActivity.friendId))){
            conversation.setUnReadMessage(0);
        }else{
            conversation.setUnReadMessage(conversation.getUnReadMessage() + 1);
        }
        conversation.setNotTroubled(false);
        conversation.setLastTimeMilli(ecMessage.getMsgTime());
        synchronized (conversations){
            conversations.add(conversation);
            if(mainActivitys != null && mainActivitys.conversationFragment!= null && mainActivitys.conversationFragment.conversationAdapter != null){
                mainActivitys.conversationFragment.conversationAdapter.notifyDataSetChanged();
            }
            Hawk.put("conversations", conversations);
        }
    }

    private void createConversation(UserInfo friend, String from, ECMessage ecMessage, Conversation conversation) {
        String time = DateUtil.converterDate(ecMessage.getMsgTime());
        conversation.setLastChatTime(time.substring(0, time.lastIndexOf(" ")));
        if(ecMessage.getType() == ECMessage.Type.TXT){
            ECTextMessageBody textMessageBody = (ECTextMessageBody) ecMessage.getBody();
            conversation.setLastMessage(textMessageBody.getMessage());
        }else if(ecMessage.getType() == ECMessage.Type.IMAGE){
            conversation.setLastMessage("图片");
        }else if(ecMessage.getType() == ECMessage.Type.VOICE){
            conversation.setLastMessage("语音");
        }else if(ecMessage.getType() == ECMessage.Type.FILE){
            conversation.setLastMessage("文件");
        }else if(ecMessage.getType() == ECMessage.Type.LOCATION){
            conversation.setLastMessage("位置");
        }else{
            conversation.setLastMessage("");
        }
        conversation.setFriend(friend);
        conversation.setFrom(from);
        if(chatActivity != null && chatActivity.messageListAdapter != null
                && conversation.getFrom().equals(("" + chatActivity.friendId))){
            conversation.setUnReadMessage(0);
        }else{
            conversation.setUnReadMessage(conversation.getUnReadMessage() + 1);
        }
        conversation.setNotTroubled(false);
        conversation.setLastTimeMilli(ecMessage.getMsgTime());
        synchronized (conversations){
            if(mainActivitys != null && mainActivitys.conversationFragment!= null && mainActivitys.conversationFragment.conversationAdapter != null){
                mainActivitys.conversationFragment.conversationAdapter.notifyDataSetChanged();
            }
        }
    }

    public void reqUserInfo(String from, ECMessage ecMessage, Conversation conversation) {
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", from)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo body) {
                        createConversation(body, from, ecMessage, conversation);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        createConversation(new UserInfo(), from, ecMessage, conversation);
                    }
                });
    }

    public Conversation getConversation(String from){
        synchronized (conversations){
            for (int i = 0; i < conversations.size(); i++) {
                Conversation conversation = conversations.get(i);
                if(conversation.getFrom().equals(from)){
                    return conversation;
                }
            }
            return null;
        }
    }

    public void updateHomeUnread() {
        int totalUnreadNum = 0;
        synchronized (conversations){
            for (int i = 0; i < conversations.size(); i++) {
                Conversation conversation = conversations.get(i);
                if(conversation != null){
                    totalUnreadNum = totalUnreadNum + conversation.getUnReadMessage();
                }
            }
            LastSiteMsg lastSiteMsg = Hawk.get("LastSiteMsg");
            if(lastSiteMsg != null){
                totalUnreadNum = totalUnreadNum + lastSiteMsg.getUnReadNum();
            }
            if(totalUnreadNum > 0 && mainActivitys != null){
                mainActivitys.unread_msg.setVisibility(View.VISIBLE);
            }else{
                mainActivitys.unread_msg.setVisibility(View.GONE);
            }
        }
    }

    public void registerChatActivity(ChatActivity chatActivity){
        this.chatActivity = chatActivity;
    }

    public void unRegisterChatActivity(){
        this.chatActivity = null;
    }

    public void registerReceiveMessage(IReceiveMessage iReceiveMessage){
        synchronized (IReceiveMessages){
            if(iReceiveMessage != null){
                IReceiveMessages.add(iReceiveMessage);
            }
        }
    }

    public void unRegisterReceiveMessage(IReceiveMessage iReceiveMessage){
        synchronized (IReceiveMessages){
            if(iReceiveMessage != null){
                IReceiveMessages.remove(iReceiveMessage);
            }
        }
    }

    public interface IReceiveMessage{
        void OnReceivedMessage(ECMessage ecMessage);
        void onReceiveOfflineMessage(List<ECMessage> list);
    }

}
