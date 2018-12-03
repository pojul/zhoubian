package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginCode;
import com.yjyc.zhoubian.model.LoginCodeModel;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.UpdateUserTime;
import com.yjyc.zhoubian.model.UpdateUserTimeModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.fragment.MeFragment;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/13/013.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_phone)
    EditText et_phone;

    @BindView(R.id.et_code)
    EditText et_code;

    @BindView(R.id.tv_code)
    TextView tv_code;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("注册及登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.tv_finish)
    public void tv_finish(){
        finish();
    }

    @OnClick(R.id.tv_code)
    public void tv_code(){
        if(et_phone.getText().toString().length() != 11){
            ToastUtils.showShort("请输入11位手机号");
            return;
        }

        loginCode();
    }

    @OnClick(R.id.tv_login)
    public void tv_login(){

        /*testLogin();
        if(true){
            return;
        }*/

        if(et_phone.getText().toString().length() != 11){
            ToastUtils.showShort("请输入11位手机号");
            return;
        }

        if(et_code.getText().toString().length() != 6){
            ToastUtils.showShort("请输入6位短信验证码");
            return;
        }
        login();
    }

    private void testLogin() {
        ToastUtils.showShort("登录成功");
        Login loginModel = new Login();
        loginModel.phone = "14787878579";
        loginModel.uid = 23;
        loginModel.token = "1d6b097181f024f899c3c59bf375e7b6";
        UserInfo userInfo = new UserInfo();
        userInfo.uid = 23;
        Hawk.put("LoginModel", loginModel);
        Hawk.put("userInfo", userInfo);

        //BaseApplication.application.loginIm(loginModel);
        finish();
    }

    private void login() {
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.LOGIN)
                .addParams("phone", et_phone.getText().toString())
                .addParams("code", et_code.getText().toString())
                .execute(new AbsJsonCallBack<LoginModel, Login>() {

                    @Override
                    public void onSuccess(Login body) {
                        reqUserInfo(body);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ProgressDialog.dismiss();
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }
                });
    }

    private void reqUserInfo(Login loginModel){
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo body) {
                        ToastUtils.showShort("登录成功");
                        loginModel.phone = et_phone.getText().toString();
                        body.uid = loginModel.uid;
                        Hawk.put("LoginModel", loginModel);
                        Hawk.put("userInfo", body);
                        updateUserTime();
                        BaseApplication.application.loginIm(loginModel);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
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
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.UPDATEUSERTIME)
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

    private void loginCode() {
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.LOGINCODE)
                .addParams("phone", et_phone.getText().toString())
                .execute(new AbsJsonCallBack<LoginCodeModel, LoginCode>() {

                    @Override
                    public void onSuccess(LoginCode body) {
                        CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(60000, 1000);
                        countDownTimerUtils.start();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    class CountDownTimerUtils extends CountDownTimer {

        public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_code.setClickable(false); //设置不可点击
            tv_code.setText(millisUntilFinished / 1000 + "秒");  //设置倒计时时间
        }

        @Override
        public void onFinish() {
            tv_code.setText("重新获取");
            tv_code.setClickable(true);//重新获得点击
        }
    }
}