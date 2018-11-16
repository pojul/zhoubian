package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;
import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.BindWx;
import com.yjyc.zhoubian.model.BindWxModel;
import com.yjyc.zhoubian.model.GetPostMsg;
import com.yjyc.zhoubian.model.GetPostMsgModel;
import com.yjyc.zhoubian.model.GetRechargeSetting;
import com.yjyc.zhoubian.model.GetRechargeSettingModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserWxInfo;
import com.yjyc.zhoubian.model.UserWxInfoModel;
import com.yjyc.zhoubian.model.WithdrawApply;
import com.yjyc.zhoubian.model.WithdrawApplyModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.fragment.PublishFragment;
import com.yjyc.zhoubian.ui.view.pickmoney.Money;
import com.yjyc.zhoubian.ui.view.pickmoney.PickMoneyView;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 提现
 * Created by Administrator on 2018/10/11/011.
 */

public class WithdrawCashActivity extends BaseActivity {

    @BindView(R.id.bind_wechat)
    TextView bindWechat;
    @BindView(R.id.user_balance)
    TextView userBalance;
    @BindView(R.id.pick_money)
    PickMoneyView pickMoney;

    private Context mContext;
    private static final int INIT = 1254;
    private Login login;
    private List<Integer> prices = new ArrayList<>();
    public static UserWxInfo userWxInfo;
    private double balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash);
        mContext = this;
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        if(login == null){
            showToast("请先登录");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        mHandler.sendEmptyMessageDelayed(INIT, 60);

    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("提现", v -> onBackPressed());
        showRightPulishText();
        TextView tv = getRightPulishText();
        tv.setText("提现记录");
        tv.setOnClickListener(view -> startActivity(new Intent(WithdrawCashActivity.this, WithdrawRecordActivity.class)));
        prices.add(1);prices.add(2);prices.add(5);prices.add(10);
        prices.add(50);prices.add(100);prices.add(500);
        pickMoney.setMoneys(Money.createMoneys(prices));
        getWxInfo();
        getPostMsg();
    }


    @OnClick({R.id.bind_wechat, R.id.withdraw_apply})
    public void click(View v){
        switch (v.getId()){
            case R.id.bind_wechat:
                bindWechat();
                break;
            case R.id.withdraw_apply:
                withdrawApply();
                break;
        }
    }

    private void withdrawApply() {
        if(userWxInfo == null || userWxInfo.is_bind == 0){
            showToast("未绑定微信号，请先绑定微信");
            return;
        }
        int money = pickMoney.gtPickedMoney();
        if(money <= 0){
            showToast("请选择提现金额");
            return;
        }
        /*if(money > balance){
            showToast("余额不足");
            return;
        }*/
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.WITHDRAWAPPLY)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .addParams("money", ("" + money))
                .execute(new AbsJsonCallBack<WithdrawApplyModel, WithdrawApply>() {
                    @Override
                    public void onSuccess(WithdrawApply body) {
                        LoadingDialog.closeLoading();
                        showToast("申请提现成功，我们将尽快处理您的提现订单");
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }
                });
    }

    private void getPostMsg() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.GETPOSTMSG)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<GetPostMsgModel, GetPostMsg>() {
                    @Override
                    public void onSuccess(GetPostMsg body) {
                        if (body == null && body.user_balance == null) {
                            userBalance.setText("当前余额：");
                            return;
                        }
                        try{
                            balance = Double.parseDouble(body.user_balance.replace("元", ""));
                        }catch (Exception e){}
                        userBalance.setText("当前余额：" + balance + "元");
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                    }
                });
    }

    private void getWxInfo(){
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERWXINFO)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<UserWxInfoModel, UserWxInfo>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast("获取数据失败");
                        finish();
                    }

                    @Override
                    public void onSuccess(UserWxInfo body) {
                        LoadingDialog.closeLoading();
                        userWxInfo = body;
                    }
                });

    }

    private void bindWechat() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        //像微信发送请求
        BaseApplication.mWxApi.sendReq(req);
    }

    private WithdrawCashActivity.MyHandler mHandler = new WithdrawCashActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<WithdrawCashActivity> activity;

        MyHandler(WithdrawCashActivity activity) {
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