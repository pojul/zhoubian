package com.yjyc.zhoubian;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.blankj.utilcode.util.BarUtils;
import com.luck.picture.lib.config.PictureConfig;
import com.orhanobut.hawk.Hawk;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.im.ECMIm;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.PostCateModel;
import com.yjyc.zhoubian.model.RedEnvelopeDistance;
import com.yjyc.zhoubian.model.RedEnvelopeDistanceModel;
import com.yjyc.zhoubian.model.RedEnvelopeSetting;
import com.yjyc.zhoubian.model.RedEnvelopeSettingModel;
import com.yjyc.zhoubian.model.SearchModel;
import com.yjyc.zhoubian.model.Searchs;
import com.yjyc.zhoubian.model.UpdateApp;
import com.yjyc.zhoubian.model.UpdateAppModel;
import com.yjyc.zhoubian.model.UpdateUserTime;
import com.yjyc.zhoubian.model.UpdateUserTimeModel;
import com.yjyc.zhoubian.model.UserGroupModel;
import com.yjyc.zhoubian.model.UserGroups;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.SetUpActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.fragment.ConversationFragment;
import com.yjyc.zhoubian.ui.fragment.MainFragment;
import com.yjyc.zhoubian.ui.fragment.MainsFragment;
import com.yjyc.zhoubian.ui.fragment.MeFragment;
import com.yjyc.zhoubian.ui.fragment.PublishFragment;
import com.yjyc.zhoubian.ui.fragment.ValuableBookFragment;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yjyc.zhoubian.utils.PermissionUtils;
import com.yjyc.zhoubian.utils.VersionUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.SdkErrorCode;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/9/009.
 */

public class MainActivitys extends AppCompatActivity {
    private static final String TAG = "MainActivitys";
    private Context mContext;

    @BindView(R.id.tv_bottom_bar_01)
    public TextView tvBottomBar01 ;

    @BindView(R.id.tv_bottom_bar_02)
    public TextView tvBottomBar02 ;

    @BindView(R.id.tv_bottom_bar_03)
    public TextView tvBottomBar03 ;

    @BindView(R.id.tv_bottom_bar_04)
    public TextView tvBottomBar04 ;

    @BindView(R.id.tv_bottom_bar_05)
    public TextView tvBottomBar05 ;

    @BindView(R.id.ivBottomBar01)
    public ImageView ivBottomBar201;

    @BindView(R.id.ivBottomBar02)
    public ImageView ivBottomBar202;

    @BindView(R.id.ivBottomBar03)
    public ImageView ivBottomBar203;

    @BindView(R.id.ivBottomBar04)
    public ImageView ivBottomBar204;

    @BindView(R.id.ivBottomBar05)
    public ImageView ivBottomBar205;

    @BindView(R.id.rl_bg)
    public RelativeLayout rl_bg;

    @BindView(R.id.unread_msg)
    public TextView unread_msg ;


    private PublishFragment publishFragment;
    private ValuableBookFragment valuableBookFragment;
    private MainsFragment mainsFragment;
    //private DopeFragment dopeFragment;
    public ConversationFragment conversationFragment;
    private MeFragment meFragment;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    public int currentTag = R.id.btn_bottom_bar_03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mains);
        mContext = this;
        ButterKnife.bind(this);
        initView();

        BaseApplication.application.initImSDK();
        ECMIm.getInstance().mainActivitys = this;
        BaseApplication.getIntstance().mainActivitys = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        BaseApplication.SCREEN_WIDTH = dm.widthPixels;
        BaseApplication.SCREEN_HEIGHT = dm.heightPixels;
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        setDefaultFragment();
        distance();
        postCate();
        getRedEnvelopeSetting();
        getRedEnvelopeDistance();
        searchTimeForDay();
        userGroup();
        updateUserTime();

        PermissionUtils.checkLocationPermission(MainActivitys.this, new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted() {
                startLocate();
                reqVersionInfo();
            }

            @Override
            public void onDenied() {
                new MaterialDialog.Builder(MainActivitys.this)
                        .title("提示")
                        .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改定位和存储权限允许访问")
                        .positiveText("去设置")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                AndPermission.permissionSetting(MainActivitys.this)
                                        .execute();
                            }
                        })
                        .show();
            }
        });
    }

    private void reqVersionInfo() {
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.UPDATEAPP)
                /*.addParams("uid", ("" + login.uid))
                .addParams("token", login.token)*/
                .execute(new AbsJsonCallBack<UpdateAppModel, UpdateApp>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(UpdateApp body) {
                        LoadingDialog.closeLoading();
                        PackageInfo packageInfo = new VersionUtil().getVersionName(MainActivitys.this);
                        if(packageInfo == null || body == null || body.app_info == null){
                            return;
                        }
                        if(body.app_info.versionCode <= packageInfo.versionCode){
                            return;
                        }
                        DialogUtil.getInstance().showUpdateAppDialog(MainActivitys.this
                                , body.app_info.versionCode, body.app_info.versionName, packageInfo.versionCode, packageInfo.versionName);
                        DialogUtil.getInstance().setDialogClick(str -> {
                            if("立即更新".equals(str)){
                                BaseApplication.getIntstance().downloadNewApk(body.app_info.downloadUrl);
                            }
                        });
                    }
                });
    }

    /**
     * 定位
     */
    private void startLocate() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        //开启定位
        mLocationClient.start();
    }

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if(location != null){
                BaseApplication.getIntstance().setLocation(location);
                BaseApplication.getIntstance().setAddress(location.getAddrStr());
                BaseApplication.getIntstance().setProvince(location.getProvince());
                BaseApplication.getIntstance().setCity(location.getCity());

                BaseApplication.myLocation = location;
            }
        }
    }

    private void setDefaultFragment() {
        setFragmentSelection(R.id.btn_bottom_bar_03);
    }


    @OnClick(R.id.btn_bottom_bar_01)
    public void btn_bottom_bar_01() {
        if(!Hawk.contains("LoginModel")){
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setFragmentSelection(R.id.btn_bottom_bar_01);
    }

    @OnClick(R.id.btn_bottom_bar_02)
    public void btn_bottom_bar_02() {
            setFragmentSelection(R.id.btn_bottom_bar_02);
    }

    @OnClick(R.id.btn_bottom_bar_03)
    public void btn_bottom_bar_03() {
            setFragmentSelection(R.id.btn_bottom_bar_03);
    }

    @OnClick(R.id.btn_bottom_bar_04)
    public void btn_bottom_bar_04() {
        if(!Hawk.contains("LoginModel")){
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setFragmentSelection(R.id.btn_bottom_bar_04);
    }

    @OnClick(R.id.btn_bottom_bar_05)
    public void btn_bottom_bar_05() {
        if(!Hawk.contains("LoginModel")){
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        setFragmentSelection(R.id.btn_bottom_bar_05);
    }

    public void setFragmentSelection(int flag) {
        // 开启一个Fragment事务
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(fragmentTransaction);
        //修改UI
        changeBottomBarState(flag);
        switch (flag) {
            case R.id.btn_bottom_bar_01:
                if (publishFragment == null) {
                    publishFragment = new PublishFragment();
                    fragmentTransaction.add(R.id.frame_content, publishFragment);
                } else {
                    fragmentTransaction.show(publishFragment);
                }
                currentTag = R.id.btn_bottom_bar_01;
                break;
            case R.id.btn_bottom_bar_02:
                if (valuableBookFragment == null) {
                    valuableBookFragment = new ValuableBookFragment();
                    fragmentTransaction.add(R.id.frame_content, valuableBookFragment);
                } else {
                    fragmentTransaction.show(valuableBookFragment);
                }
                currentTag = R.id.btn_bottom_bar_02;
                break;
            case R.id.btn_bottom_bar_03:
                if (mainsFragment == null) {
                    mainsFragment = new MainsFragment();
                    fragmentTransaction.add(R.id.frame_content, mainsFragment);
                } else {
                    fragmentTransaction.show(mainsFragment);
                }
                currentTag = R.id.btn_bottom_bar_03;
                break;
            case R.id.btn_bottom_bar_04:
                if (conversationFragment == null) {
                    conversationFragment = new ConversationFragment();
                    fragmentTransaction.add(R.id.frame_content, conversationFragment);
                } else {
                    fragmentTransaction.show(conversationFragment);
                }
                currentTag = R.id.btn_bottom_bar_04;
                break;
            case R.id.btn_bottom_bar_05:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    fragmentTransaction.add(R.id.frame_content, meFragment);
                } else {
                    fragmentTransaction.show(meFragment);
                }
                currentTag = R.id.btn_bottom_bar_05;
                break;
        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (publishFragment != null) {
            transaction.hide(publishFragment);
        }
        if (valuableBookFragment != null) {
            transaction.hide(valuableBookFragment);
        }
        if (mainsFragment != null) {
            transaction.hide(mainsFragment);
        }
        if (conversationFragment != null) {
            transaction.hide(conversationFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }
    }

    private void changeBottomBarState(int flag) {
        tvBottomBar01.setTextColor(getResources().getColor(R.color.colorMainBottomBarDefault));
        tvBottomBar02.setTextColor(getResources().getColor(R.color.colorMainBottomBarDefault));
        tvBottomBar03.setTextColor(getResources().getColor(R.color.colorMainBottomBarDefault));
        tvBottomBar04.setTextColor(getResources().getColor(R.color.colorMainBottomBarDefault));
        tvBottomBar05.setTextColor(getResources().getColor(R.color.colorMainBottomBarDefault));
        ivBottomBar201.setBackground(getResources().getDrawable(R.drawable.main_bottom_publish));
        ivBottomBar202.setBackground(getResources().getDrawable(R.drawable.main_bottom_valuablebook));
        ivBottomBar203.setBackground(getResources().getDrawable(R.drawable.main_bottom_main));
        ivBottomBar204.setBackground(getResources().getDrawable(R.drawable.main_bottom_dope));
        ivBottomBar205.setBackground(getResources().getDrawable(R.drawable.main_bottom_me));
        switch (flag) {
            case R.id.btn_bottom_bar_01:
                tvBottomBar01.setTextColor(getResources().getColor(R.color.main_bg));
                ivBottomBar201.setBackground(getResources().getDrawable(R.drawable.main_bottom_publishs));
                break;
            case R.id.btn_bottom_bar_02:
                tvBottomBar02.setTextColor(getResources().getColor(R.color.main_bg));
                ivBottomBar202.setBackground(getResources().getDrawable(R.drawable.main_bottom_valuablebooks));
                break;
            case R.id.btn_bottom_bar_03:
                tvBottomBar03.setTextColor(getResources().getColor(R.color.main_bg));
                ivBottomBar203.setBackground(getResources().getDrawable(R.drawable.main_bottom_mains));
                break;
            case R.id.btn_bottom_bar_04:
                tvBottomBar04.setTextColor(getResources().getColor(R.color.main_bg));
                ivBottomBar204.setBackground(getResources().getDrawable(R.drawable.main_bottom_dopes));
                break;
            case R.id.btn_bottom_bar_05:
                tvBottomBar05.setTextColor(getResources().getColor(R.color.main_bg));
                ivBottomBar205.setBackground(getResources().getDrawable(R.drawable.main_bottom_mes));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(publishFragment != null && resultCode == RESULT_OK && requestCode == PictureConfig.CHOOSE_REQUEST){
            publishFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Hawk.contains("LoginModel") && (currentTag == R.id.btn_bottom_bar_01 || currentTag == R.id.btn_bottom_bar_04
                || currentTag == R.id.btn_bottom_bar_05) ){
            setDefaultFragment();
        }
    }

    private void distance() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.DISTANCE)
                .execute(new AbsJsonCallBack<SearchModel, Searchs>() {


                    @Override
                    public void onSuccess(Searchs body) {
                        if(body.list == null ){
                            distance();
                            return;
                        }
                        Hawk.put("searchDistances", body.list);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        distance();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void searchTimeForDay() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.SEARCHTIMEFORDAY)
                .execute(new AbsJsonCallBack<SearchModel, Searchs>() {


                    @Override
                    public void onSuccess(Searchs body) {
                        if(body.list == null ){
                            searchTimeForDay();
                            return;
                        }
                        Hawk.put("searchTimeForDays", body.list);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        searchTimeForDay();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void userGroup() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.USERGROUP)
                .execute(new AbsJsonCallBack<UserGroupModel, UserGroups>() {
                    @Override
                    public void onSuccess(UserGroups body) {
                        if(body.list == null ){
                            userGroup();
                           return;
                       }
                        Hawk.put("userGroups", body.list);

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        userGroup();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void updateUserTime() {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            return;
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.UPDATEUSERTIME)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token + "")
                .execute(new AbsJsonCallBack<UpdateUserTimeModel, UpdateUserTime>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                    }
                    @Override
                    public void onSuccess(UpdateUserTime body) {
                        if(!body.user_state){
                            finish();
                            System.exit(0);
                        }
                    }
                });
    }

    private void getRedEnvelopeDistance() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.GETREDENVELOPEDISTANCE)
                .execute(new AbsJsonCallBack<RedEnvelopeDistanceModel, RedEnvelopeDistance[]>() {


                    @Override
                    public void onSuccess(RedEnvelopeDistance[] body) {
                        ArrayList<RedEnvelopeDistance> redEnvelopeSettings = new ArrayList<>();
                        for (RedEnvelopeDistance pc : body){
                            redEnvelopeSettings.add(pc);
                        }
                        Hawk.put("redEnvelopeDistances", redEnvelopeSettings);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        getRedEnvelopeDistance();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void getRedEnvelopeSetting() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.GETREDENVELOPESETTING)
                .execute(new AbsJsonCallBack<RedEnvelopeSettingModel, RedEnvelopeSetting[]>() {


                    @Override
                    public void onSuccess(RedEnvelopeSetting[] body) {
                        ArrayList<RedEnvelopeSetting> redEnvelopeSettings = new ArrayList<>();
                        for (RedEnvelopeSetting pc : body){
                            redEnvelopeSettings.add(pc);
                        }
                        Hawk.put("redEnvelopeSettings", redEnvelopeSettings);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        getRedEnvelopeSetting();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void postCate() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.POSTCATE)
                .execute(new AbsJsonCallBack<PostCateModel, PostCate>() {


                    @Override
                    public void onSuccess(PostCate body) {
                        if(body.list == null){
                            postCate();
                            return;
                        }
//                        ArrayList<PostCate> pcs = new ArrayList<>();
//                        for (PostCate pc : body){
//                            pcs.add(pc);
//                        }
                        Hawk.put("pcs", body.list);

                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        postCate();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    public void reLoadFragView(){
        /*现将该fragment从fragmentList移除*/
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        /*从FragmentManager中移除*/
        fragmentTransaction.remove(publishFragment);
        publishFragment = new PublishFragment();
        fragmentTransaction.add(R.id.frame_content, publishFragment);

        fragmentTransaction.commit();

    }

    public void postDownturn(int currentPos, int downturnNum){
        if(mainsFragment != null){
            mainsFragment.postDownturn(currentPos, downturnNum);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void startActivityAni(Class cls, Bundle bundle){
        Intent intent = new Intent(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.activity_move_enter_anim, R.anim.activity_scale_out_anim);
    }

}
