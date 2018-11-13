package com.yjyc.zhoubian.im.chat.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.BlackListActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.lang.ref.WeakReference;

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

    private String friendId;
    private Login login;
    private static final int INIT = 374;

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
        //sendTextMessage();

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
                if(inputEt.getText().toString().isEmpty()){
                    return;
                }
                sendTextMessage(inputEt.getText().toString());
                break;
            case R.id.hold_on_tv:
                break;
        }
    }

    private void sendTextMessage(String str) {
        try {
            ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
            msg.setTo(("" + login.uid));
            ECTextMessageBody msgBody = new ECTextMessageBody(str);
            msg.setBody(msgBody);
            ECChatManager manager = ECDevice.getECChatManager();
            manager.sendMessage(msg, messageSendListener);
        } catch (Exception e) {
            // 处理发送异常
            LogUtil.e("send message fail , e=" + e.getMessage());
        }
    }

    private ECChatManager.OnSendMessageListener messageSendListener = new ECChatManager.OnSendMessageListener() {
        @Override
        public void onSendMessageComplete(ECError error, ECMessage message) {
            // 处理消息发送结果
            LogUtil.e("onSendMessageComplete , error=" + error.errorMsg);
            if (message == null) {
                return;
            }
            // 将发送的消息更新到本地数据库并刷新UI
        }

        @Override
        public void onProgress(String msgId, int totalByte, int progressByte) {
            // 处理文件发送上传进度（尽上传文件、图片时候SDK回调该方法）
            LogUtil.e("send message progress , progressByte=" + progressByte);
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


}
