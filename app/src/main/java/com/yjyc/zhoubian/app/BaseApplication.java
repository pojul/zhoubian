package com.yjyc.zhoubian.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.maning.librarycrashmonitor.utils.MNotifyUtil;
import com.orhanobut.hawk.Hawk;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yjyc.zhoubian.Dao.MySQLiteHelper;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.ECMIm;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.im.entity.Conversation;
import com.yjyc.zhoubian.model.GetPostDetail;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.utils.Constant;
import com.yjyc.zhoubian.utils.FileUtil;
import com.yjyc.zhoubian.utils.NotificationUtil;
import com.yjyc.zhoubian.utils.SPUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.PersonInfo;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;
import com.yuqian.mncommonlibrary.MBaseManager;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @author : maning
 * @desc :
 */
public class BaseApplication extends Application {

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    private static Handler mHandler;
    public List<Activity> mActivityList = new ArrayList<>();
    public static BaseApplication application;
    public static List<SearchPosts.SearchPost> viewedPost;
    public static MainActivitys mainActivitys;
    public boolean hasInitMains;

    public static BDLocation myLocation;

    public BDLocation getLocation() {
        return location;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    private String province;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String city;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    public void setLocation(BDLocation location) {
        this.location = location;
    }
    private BDLocation location;

    public static IWXAPI mWxApi;
    public static boolean firstIn = true;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        mHandler = new Handler();
        SDKInitializer.initialize(this);
        SPUtil.Instance(getApplicationContext());
        MySQLiteHelper.Instance(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        //初始化
        MBaseManager.init(this, "---logtag---", true);
        registerToWX();
        ECMIm.Instance(getApplicationContext());
        viewedPost = Hawk.get("viewedPost");
        if(viewedPost == null){
            viewedPost = new ArrayList<>();
        }
        Collections.reverse(viewedPost);

        FileDownloader.init(getApplicationContext());
    }

    public void addViewedPost(SearchPosts.SearchPost post){
        synchronized (viewedPost){
            for (int i = 0; i < viewedPost.size(); i++) {
                if(viewedPost.get(i).id == post.id){
                    return;
                }
            }
            viewedPost.add(0, post);
            Hawk.put("viewedPost", viewedPost);
        }
    }

    private void registerToWX() {
        //第二个参数是指你应用在微信开放平台上的AppID
        mWxApi = WXAPIFactory.createWXAPI(this, null);
        // 将该app注册到微信
        mWxApi.registerApp(Constant.WEIXIN_APP_ID);
    }

    public void initImSDK(){
        if(!ECDevice.isInitialized()) {
            ECDevice.initial(getApplicationContext(), new ECDevice.InitListener() {
                @Override
                public void onInitialized() {
                    // SDK已经初始化成功
                    initIMListener();
                    Log.i("","初始化SDK成功");
                    Login login = Hawk.get("LoginModel");
                    if(login != null){
                        loginIm(login);
                    }
                }
                @Override
                public void onError(Exception exception) {
                    //在初始化错误的方法中打印错误原因
                    Log.i("","初始化SDK失败"+exception.getMessage());
                }
            });}
    }

    private void initIMListener() {
        ECDevice.setOnDeviceConnectListener(new ECDevice.OnECDeviceConnectListener() {
            public void onConnect() {
                //兼容旧版本的方法，不必处理
            }
            @Override
            public void onDisconnect(ECError error) {
                //兼容旧版本的方法，不必处理
            }

            @Override
            public void onConnectState(ECDevice.ECConnectState state, ECError error) {
                if(state == ECDevice.ECConnectState.CONNECT_FAILED ){
                    if(error.errorCode == SdkErrorCode.SDK_KICKED_OFF) {
                        LogUtil.e("==帐号异地登陆");
                    }else{
                        LogUtil.e("==其他登录失败,错误码："+ error.errorCode);
                    }
                    return ;
                }else if(state == ECDevice.ECConnectState.CONNECT_SUCCESS) {
                    SPUtil.getInstance().putInt(SPUtil.FIRST_LOGIN_IM, 1);
                    LogUtil.e("==登陆成功");
                    if(Hawk.get("LoginModel") != null && Hawk.get("userInfo") != null){
                        Login login = Hawk.get("LoginModel");
                        UserInfo userInfo = Hawk.get("userInfo");
                        setIMUserInfo(login, userInfo);
                    }
                }
            }
        });

        ECDevice.setOnChatReceiveListener(new OnChatReceiveListener() {

            int offLineCount = 0;

            @Override
            public void OnReceivedMessage(ECMessage ecMessage) {
                //toast("OnReceivedMessage");
                LogUtil.e("OnReceivedMessage");
                ECMIm.getInstance().onReceiveMessage(ecMessage);
            }

            @Override
            public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {
                //toast("onReceiveMessageNotify");
                LogUtil.e("onReceiveMessageNotify");
            }

            @Override
            public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {
                //toast("OnReceiveGroupNoticeMessage");
                LogUtil.e("OnReceiveGroupNoticeMessage");
            }

            @Override
            public void onOfflineMessageCount(int i) {
                //toast("onOfflineMessageCount");
                offLineCount = i;
                LogUtil.e("onOfflineMessageCount: " + i);
            }

            @Override
            public int onGetOfflineMessage() {
                //toast("onGetOfflineMessage");
                LogUtil.e("onGetOfflineMessage");
                return ECDevice.SYNC_OFFLINE_MSG_ALL;
            }

            @Override
            public void onReceiveOfflineMessage(List<ECMessage> list) {
                //toast("onReceiveOfflineMessage");
                //LogUtil.e("onReceiveOfflineMessage------>" + new Gson().toJson(list));
                synchronized (ECMIm.getInstance().conversations){
                    for (int i = 0; i < list.size(); i++) {
                        ECMIm.getInstance().onReceiveMessage(list.get(i));
                    }
                }
            }

            @Override
            public void onReceiveOfflineMessageCompletion() {
                //toast("onReceiveOfflineMessageCompletion");
                LogUtil.e("onReceiveOfflineMessageCompletion");
            }

            @Override
            public void onServicePersonVersion(int i) {
                //toast("onServicePersonVersion");
                LogUtil.e("onServicePersonVersion");
            }

            @Override
            public void onReceiveDeskMessage(ECMessage ecMessage) {
                //toast("onReceiveDeskMessage");
                LogUtil.e("onReceiveDeskMessage");
            }

            @Override
            public void onSoftVersion(String s, int i) {
                //toast("onSoftVersion");
                LogUtil.e("onSoftVersion");
            }
        });

    }

    public void setIMUserInfo(Login login, UserInfo userInfo) {
        if(login == null || userInfo == null){
            return;
        }
        PersonInfo personInfo =new PersonInfo();
        if(userInfo.sex == 1){
            personInfo.setSex(PersonInfo.Sex.MALE);
        }else{
            personInfo.setSex(PersonInfo.Sex.FEMALE);
        }
        if(userInfo.birthday != null){
            personInfo.setBirth(userInfo.birthday);
        }
        if(userInfo.nickname != null){
            personInfo.setNickName(userInfo.nickname);
        }
        String sign = " ; ";
        if(userInfo.sign != null){
            sign = userInfo.sign + sign;
        }
        if(userInfo.head_url_img != null){
            sign = sign + userInfo.head_url_img;
        }
        personInfo.setSign(sign);
        personInfo.setUserId((login.uid + ""));
        ECDevice.setPersonInfo(personInfo, (e, version) -> {
            if (SdkErrorCode.REQUEST_SUCCESS == e.errorCode) {
                LogUtil.e("IM set personal info success");
                return;
            }
            LogUtil.e("IM set personal info fail");
        });
    }

    public void loginIm(Login login){
        if(login == null){
            return;
        }
        ECInitParams params = ECInitParams.createParams();
        //设置用户登录账号
        params.setUserid((login.uid + ""));
        //设置AppId
        params.setAppKey("8a216da866f71d040166f7379755001f");
        //设置AppToken
        params.setToken("1bfa1326775d342a8d8f14d605d39eef");
        //设置登陆验证模式：自定义登录方式
        params.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        //LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO。使用方式详见注意事项）
        if(SPUtil.getInstance().getInt(SPUtil.FIRST_LOGIN_IM, 0) == 0){
            params.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
        }else{
            params.setMode(ECInitParams.LoginMode.AUTO);
        }
        if(params.validate()) {
            ECDevice.login(params);
        }
    }

    public void loginOutIm(){
        ECDevice.logout(() -> {
        });
    }

    public void loginOut(){
        Hawk.delete("LoginModel");
        Hawk.delete("userInfo");
        BaseApplication.getIntstance().loginOutIm();
    }

    public void downloadNewApk(String url){
        File floder = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"zhoubian");
        if(!floder.exists()){
            floder.mkdirs();
        }
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"zhoubian"+File.separator+ FileUtil.getFileName(url);
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
        FileDownloader.getImpl().create(url)
                .setPath(filePath)
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    //等待
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        //progressDialog.show();
                        NotificationUtil.notifyDownLoad(getApplicationContext(), 0);
                        Toast.makeText(BaseApplication.this, "更新包已加入后台下载列表", Toast.LENGTH_SHORT).show();
                        LogUtil.e("pending");
                    }

                    //下载进度回调
                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        //progressDialog.setProgress((soFarBytes * 100 / totalBytes));
                        int progress = (soFarBytes * 100 / totalBytes);
                        NotificationUtil.notifyDownLoad(getApplicationContext(), progress);
                        LogUtil.e("progress: " + progress);
                    }

                    //完成下载
                    @Override
                    protected void completed(BaseDownloadTask task) {
                        NotificationUtil.notifyCancelDownLoad(getApplicationContext());
                        /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                        startActivity(intent);*/
                        File apkFile = new File(filePath);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri contentUri = FileProvider.getUriForFile(
                                    getApplicationContext()
                                    , "com.yjyc.zhoubian.fileprovider"
                                    , apkFile);
                            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                        } else {
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        }
                        startActivity(intent);
                        LogUtil.e("completed");
                    }

                    //暂停
                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        LogUtil.e("paused");
                    }

                    //下载出错
                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        LogUtil.e("error" + e.getMessage());
                        NotificationUtil.notifyCancelDownLoad(getApplicationContext());
                    }

                    //已存在相同下载
                    @Override
                    protected void warn(BaseDownloadTask task) {
                        LogUtil.e("warn");
                    }
                }).start();
    }

    private void toast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static BaseApplication getIntstance() {
        return application;
    }

    public void addActivity(Activity activity) {
        mActivityList.add(activity);
    }

    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }


    public void recycleAllActivity(String other) {
        try {
            for (Activity activity : mActivityList) {
                if (activity != null && !activity.getClass().getSimpleName().equals("LoginActivity")
                        &&
                        !activity.getClass().getSimpleName().equals(other)) {
                    activity.finish();
                }
            }

            Hawk.delete("LoginModel");
            Hawk.delete("meModel");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    public static Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
