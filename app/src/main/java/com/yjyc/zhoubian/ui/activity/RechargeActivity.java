package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.AliRecharge;
import com.yjyc.zhoubian.model.AliRechargeModel;
import com.yjyc.zhoubian.model.GetRechargeSetting;
import com.yjyc.zhoubian.model.GetRechargeSettingModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.WXRecharge;
import com.yjyc.zhoubian.model.WXRechargeModel;
import com.yjyc.zhoubian.pay.alipay.PayResult;
import com.yjyc.zhoubian.utils.MD5Util;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 充值
 * Created by Administrator on 2018/10/11/011.
 */

public class RechargeActivity extends BaseActivity {

    @BindView(R.id.pay)
    TextView pay;
    @BindView(R.id.pay_by_ali)
    RadioButton payByAli;
    @BindView(R.id.pay_by_wechat)
    RadioButton payByWechat;
    @BindView(R.id.recharge_ll_1)
    LinearLayout rechargeLl1;
    @BindView(R.id.recharge_ll_2)
    LinearLayout rechargeLl2;
    @BindView(R.id.recharge_1)
    TextView recharge1;
    @BindView(R.id.recharge_2)
    TextView recharge2;
    @BindView(R.id.recharge_3)
    TextView recharge3;
    @BindView(R.id.recharge_4)
    TextView recharge4;
    @BindView(R.id.recharge_5)
    TextView recharge5;
    @BindView(R.id.recharge_6)
    TextView recharge6;
    @BindView(R.id.custom_rcharge)
    EditText customRcharge;
    @BindView(R.id.delete_custom_rcharge)
    ImageView deleteCustomRcharge;

    private static final int SDK_PAY_FLAG = 129;
    private Context mContext;
    private static final int INIT = 7246;
    private Login login;
    private List<GetRechargeSetting> fixedRecharges;
    private List<TextView> fixRechargeViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
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
        initTitleBar("充值", v -> onBackPressed());
        showRightPulishText();
        TextView tv = getRightPulishText();
        tv.setText("充值记录");
        tv.setOnClickListener(view -> startActivity(new Intent(RechargeActivity.this, RechargeRecordActivity.class)));
        fixRechargeViews.add(recharge1);
        fixRechargeViews.add(recharge2);
        fixRechargeViews.add(recharge3);
        fixRechargeViews.add(recharge4);
        fixRechargeViews.add(recharge5);
        fixRechargeViews.add(recharge6);
        payByAli.setChecked(true);
        payByAli.setOnCheckedChangeListener(payCheckListener);
        payByWechat.setOnCheckedChangeListener(payCheckListener);

        reqRechargeSetting();
    }

    private void initData() {
        if(fixedRecharges != null){
            if(fixedRecharges.size() > 0){
                rechargeLl1.setVisibility(View.VISIBLE);
            }
            if(fixedRecharges.size() > 3){
                rechargeLl2.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < fixedRecharges.size(); i++) {
                TextView rechargeView = fixRechargeViews.get(i);
                GetRechargeSetting recharge = fixedRecharges.get(i);
                if(i >= 6){
                    break;
                }
                rechargeView.setVisibility(View.VISIBLE);
                rechargeView.setText((recharge.title + ""));
            }
        }
    }

    private void reqRechargeSetting() {
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .get()
                .url(HttpUrl.GETRECHARGESETTING)
                .execute(new AbsJsonCallBack<GetRechargeSettingModel, List<GetRechargeSetting>>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<GetRechargeSetting> body) {
                        LoadingDialog.closeLoading();
                        fixedRecharges = body;
                        initData();
                    }
                });
    }

    @OnClick({R.id.pay, R.id.recharge_1, R.id.recharge_2, R.id.recharge_3, R.id.recharge_4, R.id.recharge_5,
            R.id.recharge_6, R.id.pay_by_wechat_ll, R.id.pay_by_ali_ll, R.id.delete_custom_rcharge})
    public void click(View v){
        switch (v.getId()){
            case R.id.pay:
                reqOrderInfo();
                break;
            case R.id.recharge_1:
                recharge1.setSelected(!recharge1.isSelected());
                if(recharge1.isSelected()){
                    onRechargeChanged(R.id.recharge_1);
                }
                break;
            case R.id.recharge_2:
                recharge2.setSelected(!recharge2.isSelected());
                if(recharge2.isSelected()){
                    onRechargeChanged(R.id.recharge_2);
                }
                break;
            case R.id.recharge_3:
                recharge3.setSelected(!recharge3.isSelected());
                if(recharge3.isSelected()){
                    onRechargeChanged(R.id.recharge_3);
                }
                break;
            case R.id.recharge_4:
                recharge4.setSelected(!recharge4.isSelected());
                if(recharge4.isSelected()){
                    onRechargeChanged(R.id.recharge_4);
                }
                break;
            case R.id.recharge_5:
                recharge5.setSelected(!recharge5.isSelected());
                if(recharge5.isSelected()){
                    onRechargeChanged(R.id.recharge_5);
                }
                break;
            case R.id.recharge_6:
                recharge6.setSelected(!recharge6.isSelected());
                if(recharge6.isSelected()){
                    onRechargeChanged(R.id.recharge_6);
                }
                break;
            case R.id.pay_by_wechat_ll:
                payByWechat.setChecked(true);
                break;
            case R.id.pay_by_ali_ll:
                payByAli.setChecked(true);
                break;
            case R.id.delete_custom_rcharge:
                customRcharge.setText("");
                break;
        }
    }

    private void onRechargeChanged(int id) {
        for (int i = 0; i < fixRechargeViews.size(); i++) {
            TextView fixedRechargeView = fixRechargeViews.get(i);
            if(fixedRechargeView.getId() == id){
                continue;
            }
            fixedRechargeView.setSelected(false);
        }
    }

    private CompoundButton.OnCheckedChangeListener payCheckListener = (view, isChecked) -> {
        if(!isChecked){
            return;
        }
        switch (view.getId()){
            case R.id.pay_by_ali:
                payByWechat.setChecked(false);
                break;
            case R.id.pay_by_wechat:
                payByAli.setChecked(false);
                break;
        }
    };

    private void reqOrderInfo() {
        double recharge = getRecharge();
        LogUtil.e(("reqOrderInfo: " + recharge));
        if(recharge < 0){
            showToast("请选择充值金额");
            return;
        }
        LoadingDialog.showLoading(this);
        OkhttpUtils okhttpUtils = OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERRECHARGE)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("money", ("" + 0.01));
        if(payByAli.isChecked()){
            okhttpUtils.addParams("pay_type", ("" + 2));
            okhttpUtils.execute(new AbsJsonCallBack<AliRechargeModel, AliRecharge>() {
                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    LoadingDialog.closeLoading();
                    showToast(errorMsg);
                }

                @Override
                public void onSuccess(AliRecharge body) {
                    LoadingDialog.closeLoading();
                    if(body.pay_result == null || body.pay_result.isEmpty()){
                        showToast("请求错误");
                        return;
                    }
                    aliPay(body.pay_result);
                }
            });
        }else{
            okhttpUtils.addParams("pay_type", ("" + 1));
            okhttpUtils.execute(new AbsJsonCallBack<WXRechargeModel, WXRecharge>() {
                @Override
                public void onFailure(String errorCode, String errorMsg) {
                    LoadingDialog.closeLoading();
                    showToast(errorMsg);
                }

                @Override
                public void onSuccess(WXRecharge body) {
                    LoadingDialog.closeLoading();
                    if(body == null){
                        showToast("请求错误");
                        return;
                    }
                    wxPay(body);
                }
            });
        }

    }

    private void wxPay(WXRecharge body) {
        PayReq request = new PayReq();
        request.appId = body.pay_result.appId;
        request.partnerId = body.pay_result.partnerId;
        request.prepayId= body.pay_result.prepayId.split("=")[1];
        request.packageValue = "Sign=WXPay";
        request.nonceStr= body.pay_result.nonceStr;
        request.timeStamp= body.pay_result.timeStamp;
        /*request.sign = body.pay_result.paySign;
        request.signType = body.pay_result.signType;*/

        // 把参数的值传进去SortedMap集合里面
        SortedMap<Object, Object> parameters = new TreeMap<Object, Object>();
        parameters.put("appid", body.pay_result.appId);
        parameters.put("noncestr", body.pay_result.nonceStr);
        parameters.put("package", "Sign=WXPay");
        parameters.put("partnerid", body.pay_result.partnerId);
        parameters.put("prepayid", body.pay_result.prepayId);
        parameters.put("timestamp", body.pay_result.timeStamp);

        String characterEncoding = "UTF-8";
        String mySign = createSign(characterEncoding, parameters);
        System.out.println("我的签名是：" + mySign);
        request.sign = mySign;

        BaseApplication.mWxApi.sendReq(request);
    }

    public static String createSign(String characterEncoding,
                                    SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();// 所有参与传参的参数按照accsii排序（升序）
        Iterator it = es.iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k)
                    && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + "50faef596bd1302f2970252ef170e04f"); //KEY是商户秘钥
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding)
                .toUpperCase();
        return sign;
    }

    private double getRecharge() {
        for (int i = 0; i < fixRechargeViews.size(); i++) {
            TextView fixedRechargeView = fixRechargeViews.get(i);
            if(fixedRechargeView.isSelected()){
                try{
                    return Double.parseDouble(fixedRechargeView.getText().toString());
                }catch(Exception e){
                    return -1;
                }
            }
        }
        if(!customRcharge.getText().toString().isEmpty()){
            try{
                return Double.parseDouble(customRcharge.getText().toString());
            }catch(Exception e){
                return -1;
            }
        }
        return -1;
    }

    private void aliPay(String orderInfo){
        Runnable payRunnable = () -> {
            //新建任务
            PayTask alipay = new PayTask(RechargeActivity.this);
            //获取支付结果
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private RechargeActivity.MyHandler mHandler = new RechargeActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<RechargeActivity> activity;

        MyHandler(RechargeActivity activity) {
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
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(activity.get(), "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(activity.get(), "支付失败", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


}