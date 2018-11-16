package com.yjyc.zhoubian.wxapi;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.BindWx;
import com.yjyc.zhoubian.model.BindWxModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.WithdrawCashActivity;
import com.yjyc.zhoubian.utils.Constant;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import org.json.JSONException;
import org.json.JSONObject;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXEntryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        //这句没有写,是不能执行回调的方法的
        BaseApplication.mWxApi.handleIntent(getIntent(), this);
    }


    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                String code = ((SendAuth.Resp) baseResp).code;
                //获取用户信息
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED://用户拒绝授权
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL://用户取消
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
            default:
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
    }
    private void getAccessToken(String code) {
        LoadingDialog.showLoading(this);
        //获取授权
        StringBuffer loginUrl = new StringBuffer();
        loginUrl.append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=")
                .append(Constant.WEIXIN_APP_ID)
                .append("&secret=")
                .append(Constant.WEIXIN_APP_SECRET)
                .append("&code=")
                .append(code)
                .append("&grant_type=authorization_code");
        OkHttpUtils.ResultCallback<String> resultCallback = new OkHttpUtils.ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                LoadingDialog.closeLoading();
                String access = null;
                String openId = null;
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    access = jsonObject.getString("access_token");
                    openId = jsonObject.getString("openid");
                } catch (JSONException e) {
                    Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                    finish();
                    //e.printStackTrace();
                }
                //获取个人信息
                LoadingDialog.showLoading(WXEntryActivity.this);
                String getUserInfo = "https://api.weixin.qq.com/sns/userinfo?access_token=" + access + "&openid=" + openId;
                OkHttpUtils.ResultCallback<String> reCallback = new OkHttpUtils.ResultCallback<String>() {
                    @Override
                    public void onSuccess(String responses) {
                        LoadingDialog.closeLoading();
                        String nickName = null;
                        String sex = null;
                        String city = null;
                        String province = null;
                        String country = null;
                        String headimgurl = null;
                        String openidStr = null;
                        try {
                            JSONObject jsonObject = new JSONObject(responses);
                            openidStr = jsonObject.getString("openid");
                            nickName = jsonObject.getString("nickname");
                            sex = jsonObject.getString("sex");
                            city = jsonObject.getString("city");
                            province = jsonObject.getString("province");
                            country = jsonObject.getString("country");
                            headimgurl = jsonObject.getString("headimgurl");
                            String unionid = jsonObject.getString("unionid");
                            /*loadNetData(1, openid, nickName, sex, province,
                                    city, country, headimgurl, unionid);*/
                            Log.e(TAG, "openid: " + openidStr + "; nickName" + nickName + "; headimgurl" + headimgurl + "unionid: " + unionid);
                            bindWechat(openidStr, nickName);
                        } catch (JSONException e) {
                            //e.printStackTrace();
                            Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                };
                OkHttpUtils.get(getUserInfo, reCallback);
            }

            @Override
            public void onFailure(Exception e) {
                LoadingDialog.closeLoading();
                Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                finish();
            }
        };
        OkHttpUtils.get(loginUrl.toString(), resultCallback);
    }

    private void bindWechat(String openId, String nickName){
        Login login = Hawk.get("LoginModel");
        UserInfo userInfo = Hawk.get("userInfo");
        if(login == null || userInfo == null || openId == null){
            Toast.makeText(WXEntryActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.BINDWX)
                .addParams("uid", ("" + login.uid))
                .addParams("token", ("" + login.token))
                .addParams("openid", openId)
                .addParams("wx_nickname", nickName)
                .execute(new AbsJsonCallBack<BindWxModel, BindWx>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(WXEntryActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    @Override
                    public void onSuccess(BindWx body) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(WXEntryActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                        WithdrawCashActivity.userWxInfo.is_bind = 1;
                        WithdrawCashActivity.userWxInfo.wx_nickname = nickName;
                        finish();
                    }
                });
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }



}

