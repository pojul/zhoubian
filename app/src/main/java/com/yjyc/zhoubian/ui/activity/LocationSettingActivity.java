package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.GetUserPosition;
import com.yjyc.zhoubian.model.GetUserPositionModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.OpenDefaultPosition;
import com.yjyc.zhoubian.model.OpenDefaultPositionModel;
import com.yjyc.zhoubian.model.RemovePosition;
import com.yjyc.zhoubian.model.RemovePositionModel;
import com.yjyc.zhoubian.model.ShutDefaultPosition;
import com.yjyc.zhoubian.model.ShutDefaultPositionModel;
import com.yjyc.zhoubian.model.UserPosition;
import com.yjyc.zhoubian.model.UserPositionModel;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationSettingActivity extends BaseActivity {


    @BindView(R.id.loc_bt_a)
    Button locBtA;
    @BindView(R.id.loc_bt_b)
    Button locBtB;
    @BindView(R.id.loc_bt_c)
    Button locBtC;
    @BindView(R.id.loc_addr_a)
    TextView locAddrA;
    @BindView(R.id.loc_addr_b)
    TextView locAddrB;
    @BindView(R.id.loc_addr_c)
    TextView locAddrC;
    @BindView(R.id.loc_switch_a)
    Switch locSwitchA;
    @BindView(R.id.loc_switch_b)
    Switch locSwitchB;
    @BindView(R.id.loc_switch_c)
    Switch locSwitchC;
    @BindView(R.id.clear_a)
    TextView clearA;
    @BindView(R.id.clear_b)
    TextView clearB;
    @BindView(R.id.clear_c)
    TextView clearC;


    private static final int INIT = 8246;
    private static final int ADD_SWITCH_LISTENER = 8247;
    private static final int REMOVE_SWITCH_LISTENER = 8248;
    private Login login;
    private UserPosition positionA;
    private UserPosition positionB;
    private UserPosition positionC;
    private int getLocResultType = 0; //1:PositionA; 2:PositionB; 3:PositionB;
    private static final int REQUEST_GETLOC_CODE = 212;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_setting);
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        if(login == null){
            showToast("请先登录");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        initTitleBar("定位设置", v -> onBackPressed());
        mHandler.sendEmptyMessageDelayed(INIT, 100);

    }

    private void initView() {
        reqLocPosition();
    }

    private void initData(){
        if(positionA.position != null){
            locAddrA.setText(positionA.position);
        }else{
            locAddrA.setText("");
        }
        if(positionB.position != null){
            locAddrB.setText(positionB.position);
        }else{
            locAddrB.setText("");
        }
        if(positionC.position != null){
            locAddrC.setText(positionC.position);
        }else{
            locAddrC.setText("");
        }

        if(!"待获取".equals(positionA.position)){
            clearA.setVisibility(View.VISIBLE);
        }else{
            clearA.setVisibility(View.GONE);
        }
        if(!"待获取".equals(positionB.position)){
            clearB.setVisibility(View.VISIBLE);
        }else{
            clearB.setVisibility(View.GONE);
        }
        if(!"待获取".equals(positionC.position)){
            clearC.setVisibility(View.VISIBLE);
        }else{
            clearC.setVisibility(View.GONE);
        }
        updateSwitch();
    }

    private void updateSwitch() {
        if(positionA.status == 1){
            locSwitchA.setChecked(true);
            locSwitchB.setChecked(false);
            locSwitchC.setChecked(false);
        }else if(positionB.status == 1){
            locSwitchA.setChecked(false);
            locSwitchB.setChecked(true);
            locSwitchC.setChecked(false);
        }else if(positionC.status == 1){
            locSwitchA.setChecked(false);
            locSwitchB.setChecked(false);
            locSwitchC.setChecked(true);
        }else{
            locSwitchA.setChecked(false);
            locSwitchB.setChecked(false);
            locSwitchC.setChecked(false);
        }
    }

    private void reqLocPosition() {
        LoadingDialog.showLoading(this);
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERPOSITION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<UserPositionModel, List<UserPosition>>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                        finish();
                    }

                    @Override
                        public void onSuccess(List<UserPosition> body) {
                        LoadingDialog.closeLoading();
                        setPotions(body);
                        if(positionA == null || positionB == null || positionC == null){
                            showToast("数据错误");
                            finish();
                            return;
                        }
                        initData();
                        addSwitchListener();
                    }
                });

    }

    private void setPotions(List<UserPosition> body) {
        for (int i = 0; i < body.size(); i++) {
            UserPosition tempPosition = body.get(i);
            if(tempPosition == null){
                continue;
            }
            if(tempPosition.title.equals("地址A")){
                positionA = tempPosition;
                continue;
            }
            if(tempPosition.title.equals("地址B")){
                positionB = tempPosition;
                continue;
            }
            if(tempPosition.title.equals("地址C")){
                positionC = tempPosition;
                continue;
            }
        }
    }


    @OnClick({R.id.loc_bt_a, R.id.loc_bt_b, R.id.loc_bt_c, R.id.clear_a, R.id.clear_b, R.id.clear_c})
    public void click(View v){
        switch (v.getId()){
            case R.id.loc_bt_a:
                getLocResultType = 1;
                getPosition();
                break;
            case R.id.loc_bt_b:
                getLocResultType = 2;
                getPosition();
                break;
            case R.id.loc_bt_c:
                getLocResultType = 3;
                getPosition();
                break;
            case R.id.clear_a:
                //先关闭使用状态
                if(!"待获取".equals(positionA.position)){
                    clearPosition(positionA.id);
                }
                break;
            case R.id.clear_b:
                if(!"待获取".equals(positionB.position)){
                    clearPosition(positionB.id);
                }
                break;
            case R.id.clear_c:
                if(!"待获取".equals(positionC.position)){
                    clearPosition(positionC.id);
                }
                break;
        }
    }

    public void clearPosition(int id){
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.REMOVEPOSITION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("position_id", ("" + id))
                .execute(new AbsJsonCallBack<RemovePositionModel, RemovePosition>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(RemovePosition body) {
                        LoadingDialog.closeLoading();
                        if(id == positionA.id){
                            positionA.position = "待获取";
                            positionA.lat = 0;
                            positionA.lon = 0;
                            positionA.status = 0;
                        }else if(id == positionB.id){
                            positionB.position = "待获取";
                            positionB.lat = 0;
                            positionB.lon = 0;
                            positionB.status = 0;
                        }else if(id == positionC.id){
                            positionC.position = "待获取";
                            positionC.lat = 0;
                            positionC.lon = 0;
                            positionC.status = 0;
                        }
                        removeSwitchListener();
                        initData();
                        addSwitchListener();
                    }
                });
    }

    public void getPosition(){
        Intent intent = new Intent(this, GetLocActivity.class);
        LatLng lat = null;
        if(getLocResultType == 1 && !"待获取".equals(positionA.position)){
            lat = new LatLng(positionA.lat, positionA.lon);
            intent.putExtra("rawLoc", new Gson().toJson(lat));
        }
        if(getLocResultType == 2 && !"待获取".equals(positionB.position)){
            lat = new LatLng(positionB.lat, positionB.lon);
            intent.putExtra("rawLoc", new Gson().toJson(lat));
        }
        if(getLocResultType == 3 && !"待获取".equals(positionC.position)){
            lat = new LatLng(positionC.lat, positionC.lon);
            intent.putExtra("rawLoc", new Gson().toJson(lat));
        }
        if(lat == null && BaseApplication.getIntstance().getLocation() != null){
            BDLocation mBDLocation = BaseApplication.getIntstance().getLocation();
            lat = new LatLng(mBDLocation.getLatitude(), mBDLocation.getLongitude());
            intent.putExtra("rawLoc", new Gson().toJson(lat));
        }
        startActivityForResult(intent, REQUEST_GETLOC_CODE);
    }

    private CompoundButton.OnCheckedChangeListener switchCheckListener = (view, isChecked) -> {
        switch (view.getId()){
            case R.id.loc_switch_a:
                if(isChecked){
                    if("待获取".equals(positionA.position)){
                        getLocResultType = 1;
                        getPosition();
                        locSwitchA.setChecked(false);
                        return;
                    }else{
                        openDefaultPosition(positionA.id);
                    }
                }else{
                    shutDefaultPosition(positionA.id);
                }
                break;
            case R.id.loc_switch_b:
                if(isChecked){
                    if("待获取".equals(positionB.position)){
                        getLocResultType = 2;
                        getPosition();
                        locSwitchB.setChecked(false);
                        return;
                    }else{
                        openDefaultPosition(positionB.id);
                    }
                }else{
                    shutDefaultPosition(positionB.id);
                }
                break;
            case R.id.loc_switch_c:
                if(isChecked){
                    if("待获取".equals(positionC.position)){
                        getLocResultType = 3;
                        getPosition();
                        locSwitchC.setChecked(false);
                        return;
                    }else{
                        openDefaultPosition(positionC.id);
                    }
                }else{
                    shutDefaultPosition(positionC.id);
                }
                break;
        }
    };

    private void openDefaultPosition(int id){
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.OPENDEFAULTPOSITION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("position_id", ("" + id))
                .execute(new AbsJsonCallBack<OpenDefaultPositionModel, OpenDefaultPosition>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(OpenDefaultPosition body) {
                        LoadingDialog.closeLoading();
                        if(id == positionA.id){
                            positionA.status = 1;
                            positionB.status = 0;
                            positionC.status = 0;
                        }else if(id == positionB.id){
                            positionA.status = 0;
                            positionB.status = 1;
                            positionC.status = 0;
                        }else if(id == positionC.id){
                            positionA.status = 0;
                            positionB.status = 0;
                            positionC.status = 1;
                        }
                        removeSwitchListener();
                        updateSwitch();
                        addSwitchListener();
                    }
                });
    }

    private void shutDefaultPosition(int id){
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.SHUTDEFAULTPOSITION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("position_id", ("" + id))
                .execute(new AbsJsonCallBack<ShutDefaultPositionModel, ShutDefaultPosition>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(ShutDefaultPosition body) {
                        LoadingDialog.closeLoading();
                        if(id == positionA.id){
                            positionA.status = 0;
                        }else if(id == positionB.id){
                            positionB.status = 0;
                        }else if(id == positionC.id){
                            positionC.status = 0;
                        }
                        removeSwitchListener();
                        updateSwitch();
                        addSwitchListener();
                    }
                });
    }

    private void removeSwitchListener(){
        locSwitchA.setOnCheckedChangeListener(null);
        locSwitchB.setOnCheckedChangeListener(null);
        locSwitchC.setOnCheckedChangeListener(null);
    }

    private void addSwitchListener(){
        locSwitchA.setOnCheckedChangeListener(switchCheckListener);
        locSwitchB.setOnCheckedChangeListener(switchCheckListener);
        locSwitchC.setOnCheckedChangeListener(switchCheckListener);
    }

    private void uploadPosition(ReverseGeoCodeResult locResult) {
        int titleId = -1;
        if(getLocResultType == 1){
            titleId = positionA.id;
        }else if(getLocResultType == 2){
            titleId = positionB.id;
        }else if(getLocResultType == 3){
            titleId = positionC.id;
        }
        if(titleId == -1){
            return;
        }
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.GETUSERPOSITION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("id", ("" + titleId))
                .addParams("lon", ("" + locResult.getLocation().longitude))
                .addParams("lat", ("" + locResult.getLocation().latitude))
                .addParams("position", locResult.getAddress())
                .addParams("province", locResult.getAddressDetail().province)
                .addParams("city", locResult.getAddressDetail().city)
                .execute(new AbsJsonCallBack<GetUserPositionModel, GetUserPosition>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                        getLocResultType = 0;
                    }
                    @Override
                    public void onSuccess(GetUserPosition body) {
                        LoadingDialog.closeLoading();
                        if(getLocResultType == 1){
                            positionA.lon = locResult.getLocation().longitude;
                            positionA.lat = locResult.getLocation().latitude;
                            positionA.position = locResult.getAddress();
                        }else if(getLocResultType == 2){
                            positionB.lon = locResult.getLocation().longitude;
                            positionB.lat = locResult.getLocation().latitude;
                            positionB.position = locResult.getAddress();
                        }else if(getLocResultType == 3){
                            positionC.lon = locResult.getLocation().longitude;
                            positionC.lat = locResult.getLocation().latitude;
                            positionC.position = locResult.getAddress();
                        }
                        getLocResultType = 0;
                        initData();
                    }
                });
    }

    private LocationSettingActivity.MyHandler mHandler = new LocationSettingActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<LocationSettingActivity> activity;
        MyHandler(LocationSettingActivity activity) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_GETLOC_CODE && resultCode == RESULT_OK){
            String locResultStr = data.getStringExtra("locResult");
            try{
                ReverseGeoCodeResult locResult = new Gson().fromJson(locResultStr, ReverseGeoCodeResult.class);
                uploadPosition(locResult);
            }catch (Exception e){}
        }
    }
}
