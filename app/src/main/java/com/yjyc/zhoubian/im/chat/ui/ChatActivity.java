package com.yjyc.zhoubian.im.chat.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.Dao.ChatMessageDao;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.im.ECMIm;
import com.yjyc.zhoubian.im.chat.adapter.MessageListAdapter;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.GetChatState;
import com.yjyc.zhoubian.model.GetChatStateModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.view.MyClassicsHeader;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yjyc.zhoubian.utils.FileUtil;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends BaseActivity {

    /*@BindView(R.id.voice_hold)
    ImageView voiceHold;*/
    @BindView(R.id.input_et)
    EditText inputEt;
    /*@BindView(R.id.emoji)
    ImageView emoji;*/
    @BindView(R.id.more_message)
    ImageView moreMessage;
    @BindView(R.id.send)
    Button send;
    @BindView(R.id.hold_on_tv)
    TextView holdOnTv;
    @BindView(R.id.message_list)
    RecyclerView messageList;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.tv_title)
    TextView tv_title;

    public String friendId;
    private Login login;
    private UserInfo owner;
    private UserInfo friend;
    private static final int INIT = 374;
    public MessageListAdapter messageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
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
        /*mHandler.sendEmptyMessageDelayed(INIT, 100);*/
        ECMIm.getInstance().registerChatActivity(this);
        initView();
    }

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

        refreshLayout.setRefreshHeader(new MyClassicsHeader(this));
        refreshLayout.setEnableLoadmore(false);
        ((MyClassicsHeader)refreshLayout.getRefreshHeader()).showLoadOnly();
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadLocalMess();
        });

        reqInInBlackList();
    }

    private void loadLocalMess() {
        long time = messageListAdapter.getTopTime();
        List<ECMessage> messages = new ChatMessageDao().getHistoryMessage(friendId,(login.uid + ""), time, 10);
        messageListAdapter.addHistoryMessage(messages);
        refreshLayout.finishRefresh();
    }

    private void reqInInBlackList() {
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.GETCHATSTATE)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .addParams("chatUid", friendId)
                .execute(new AbsJsonCallBack<GetChatStateModel, GetChatState>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast("数据错误");
                        finish();
                    }

                    @Override
                    public void onSuccess(GetChatState body) {
                        LoadingDialog.closeLoading();
                        if(body.chat_state){
                            showToast("您已被拉黑");
                            finish();
                        }
                        reqFriendInfo(friendId);
                    }
                });
    }

    private void reqFriendInfo(String friendId) {
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", friendId + "")
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo body) {
                        friend = body;
                        friend.uid = Integer.parseInt(friendId);
                        messageListAdapter = new MessageListAdapter(ECMIm.getInstance().getChatRoomMessages(friendId), ChatActivity.this, owner, friend);
                        messageList.setAdapter(messageListAdapter);
                        String title;
                        if(friend.nickname != null && !friend.nickname.isEmpty()){
                            title = friend.nickname;
                        }else{
                            title = "佚名";
                        }
                        tv_title.setText(title);
                        ECMIm.getInstance().clearConversationUndead(friendId);
                        loadLocalMess();
                        new Handler(Looper.getMainLooper()).postDelayed(()->{
                            smoothToBottom(messageListAdapter.datas.size());
                        },300);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        finish();
                    }
                });
    }

    @OnClick({R.id.send, R.id.hold_on_tv, R.id.iv_left, R.id.more_message})
    public void click(View v){
        switch (v.getId()){
            /*case R.id.voice_hold:
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
                break;*/
            case R.id.more_message:
                pickPic();
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
            case R.id.iv_left:
                finish();
                break;
        }
    }

    @SuppressLint("CheckResult")
    private void pickPic() {
        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if(!granted){
                        new MaterialDialog.Builder(mContext)
                                .title("提示")
                                .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改相机和存储权限允许访问")
                                .positiveText("去设置")
                                .negativeText("取消")
                                .onPositive((dialog, which) -> AndPermission.permissionSetting(mContext)
                                        .execute())
                                .show();
                    }else{
                        PictureSelector.create(this)
                                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                .maxSelectNum(1)// 最大图片选择数量 int
                                .minSelectNum(1)// 最小选择数量 int
                                .imageSpanCount(4)// 每行显示个数 int
                                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(true)// 是否可预览图片 true or false
                                .isCamera(true)// 是否显示拍照按钮 true or false
                                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                                .sizeMultiplier(0.8f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .compress(true)// 是否压缩 true or false
                                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                .minimumCompressSize(300)// 小于300kb的图片不压缩
                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if(selectList != null && selectList.size() > 0){
                        sendPicMessage(selectList.get(0).getPath());
                    }
                    break;
            }
        }
    }

    private void sendPicMessage(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                showToast("文件不存在");
                return;
            }
            ECMessage msg = ECMessage.createECMessage(ECMessage.Type.IMAGE);
            ECImageMessageBody msgBody = new ECImageMessageBody();
            msgBody.setFileName(FileUtil.getFileName(path));
            msgBody.setFileExt(FileUtil.getFileType(path));
            msgBody.setLocalUrl(path);
            msg.setTo(("" + friendId));
            msg.setBody(msgBody);
            ECChatManager manager = ECDevice.getECChatManager();
            manager.sendMessage(msg, messageSendListener);
            messageListAdapter.addMessage(msg);
            checkConversation(msg);
        } catch (Exception e) {
            LogUtil.e("send message fail , e=" + e.getMessage());
            com.yuqian.mncommonlibrary.utils.ToastUtils.show(e.getMessage());
            onSendError();
        }
    }


    private void sendTextMessage(String str) {
        try {
            ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
            msg.setTo(("" + friendId));
            ECTextMessageBody msgBody = new ECTextMessageBody(str);
            msg.setBody(msgBody);
            ECChatManager manager = ECDevice.getECChatManager();
            com.yuqian.mncommonlibrary.utils.ToastUtils.show("manager: " + manager + "; msg: " + msg + "; messageSendListener: " + messageSendListener);
            manager.sendMessage(msg, messageSendListener);
            messageListAdapter.addMessage(msg);
            checkConversation(msg);
        } catch (Exception e) {
            LogUtil.e("send message fail , e=" + e.getMessage());
            com.yuqian.mncommonlibrary.utils.ToastUtils.show(e.getMessage());
            onSendError();
        }
    }




    private void onSendError() {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            startActivity(new Intent(this, LoginActivity.class));
            showToast("请先登录");
            return;
        }
        if(!ECDevice.isInitialized()){
            BaseApplication.getIntstance().initImSDK();
        }else{
            BaseApplication.getIntstance().loginIm(login);
        }
    }

    public void smoothToBottom(int pos){
        messageList.smoothScrollToPosition(pos);
    }

    private void checkConversation(ECMessage ecMessage) {
        if(ECMIm.getInstance().getConversation(friendId) == null){
            ECMIm.getInstance().createConversation(friend, (friendId + ""), ecMessage);
        }
    }

    private ECChatManager.OnSendMessageListener messageSendListener = new ECChatManager.OnSendMessageListener() {
        @Override
        public void onSendMessageComplete(ECError error, ECMessage message) {
            LogUtil.e("onSendMessageComplete , error=" + error.errorMsg);
            if (message == null) {
                return;
            }
            new ChatMessageDao().insertMessage(new ChatMessage(message));
            messageListAdapter.notifyMessageStatus(message);
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

    @Override
    protected void onDestroy() {
        ECMIm.getInstance().unRegisterChatActivity();
        super.onDestroy();
    }
}
