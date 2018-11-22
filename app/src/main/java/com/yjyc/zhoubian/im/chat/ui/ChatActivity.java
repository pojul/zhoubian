package com.yjyc.zhoubian.im.chat.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.MessageListAdapter;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.BlackListActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    @BindView(R.id.voice_hold)
    ImageView voiceHold;
    @BindView(R.id.input_et)
    EditText inputEt;
    @BindView(R.id.emoji)
    ImageView emoji;
    @BindView(R.id.more_message)
    ImageView moreMessage;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.hold_on_tv)
    TextView holdOnTv;
    @BindView(R.id.message_list)
    RecyclerView messageList;

    private String friendId;
    private Login login;
    private UserInfo owner;
    private UserInfo friend;
    private static final int INIT = 374;
    private MessageListAdapter messageListAdapter;
    private List<ChatMessage> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        if(login == null){
            startActivity(new Intent(this, LoginActivity.class));
            showToast("请先登录");
            finish();
            return;
        }
        friendId = getIntent().getStringExtra("frindId");
        if(friendId == null || friendId.equals(("" + login.uid)) ){
            finish();
            return;
        }

        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

   /* private void showSendBt(){

    }

    private void hideSendBt(){

    }*/

    private void initView(){
        inputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(inputEt.getText().toString().isEmpty()){
                    send.setVisibility(View.GONE);
                    moreMessage.setVisibility(View.VISIBLE);
                }else{
                    send.setVisibility(View.VISIBLE);
                    moreMessage.setVisibility(View.INVISIBLE);
                }
            }
        });
        owner = Hawk.get("userInfo");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageList.setLayoutManager(layoutManager);
        reqFriendInfo(friendId);
        BaseApplication.application.registerReceiveMessage(iReceiveMessage);
    }

    private void reqFriendInfo(String friendId) {
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", friendId + "")
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo body) {
                        LoadingDialog.closeLoading();
                        friend = body;
                        try{
                            friend.uid = Integer.parseInt(friendId);
                        }catch(Exception e){
                            showToast("数据错误");
                            finish();
                        }
                        messageListAdapter = new MessageListAdapter(datas, ChatActivity.this, owner, friend);
                        messageList.setAdapter(messageListAdapter);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        finish();
                    }
                });
    }

    @OnClick({R.id.voice_hold, R.id.emoji, R.id.more_message, R.id.send, R.id.hold_on_tv})
    public void click(View v){
        switch (v.getId()){
            case R.id.voice_hold:
                voiceHold.setSelected(!voiceHold.isSelected());
                if(voiceHold.isSelected()){
                    holdOnTv.setVisibility(View.VISIBLE);
                    inputEt.setVisibility(View.GONE);
                }else{
                    holdOnTv.setVisibility(View.GONE);
                    inputEt.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.emoji:
                break;
            case R.id.more_message:
                break;
            case R.id.send:
                if(inputEt.getText().toString().isEmpty() || holdOnTv.getVisibility() == View.VISIBLE){
                    return;
                }
                sendTextMessage(inputEt.getText().toString());
                inputEt.setText("");
                break;
            case R.id.hold_on_tv:
                break;
        }
    }

    private void sendTextMessage(String str) {
        try {
            ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
            msg.setTo(("" + friendId));
            ECTextMessageBody msgBody = new ECTextMessageBody(str);
            msg.setBody(msgBody);
            ECChatManager manager = ECDevice.getECChatManager();
            manager.sendMessage(msg, messageSendListener);
            messageListAdapter.addMessage(msg);
        } catch (Exception e) {
            LogUtil.e("send message fail , e=" + e.getMessage());
        }
    }

    private ECChatManager.OnSendMessageListener messageSendListener = new ECChatManager.OnSendMessageListener() {
        @Override
        public void onSendMessageComplete(ECError error, ECMessage message) {
            LogUtil.e("onSendMessageComplete , error=" + error.errorMsg);
            messageListAdapter.notifyMessageStatus(message);
            if (message == null) {
                return;
            }
        }

        @Override
        public void onProgress(String msgId, int totalByte, int progressByte) {
            // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            LogUtil.e("send message progress , progressByte=" + progressByte);
        }
    };

    private BaseApplication.IReceiveMessage iReceiveMessage = new BaseApplication.IReceiveMessage() {
        @Override
        public void OnReceivedMessage(ECMessage ecMessage) {
            messageListAdapter.addMessage(ecMessage);
        }

        @Override
        public void onReceiveOfflineMessage(List<ECMessage> list) {
            messageListAdapter.addMessage(list);
        }
    };

    private ChatActivity.MyHandler mHandler = new ChatActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<ChatActivity> activity;
        MyHandler(ChatActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().initView();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        BaseApplication.application.unRegisterReceiveMessage(iReceiveMessage);
        super.onDestroy();
    }
}
