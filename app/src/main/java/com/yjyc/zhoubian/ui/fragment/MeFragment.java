package com.yjyc.zhoubian.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.activity.DetailActivity;
import com.yjyc.zhoubian.ui.activity.DraftsActivity;
import com.yjyc.zhoubian.ui.activity.EditProfileActivity;
import com.yjyc.zhoubian.ui.activity.MyCollectActivity;
import com.yjyc.zhoubian.ui.activity.MyFootprintActivity;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.activity.RechargeActivity;
import com.yjyc.zhoubian.ui.activity.SetUpActivity;
import com.yjyc.zhoubian.ui.activity.WithdrawCashActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/10/9/009.
 */

public class MeFragment extends Fragment{
    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_cty)
    TextView tv_cty;

    @BindView(R.id.tv_sign)
    TextView tv_sign;

    @BindView(R.id.tv_balance)
    TextView tv_balance;

    @BindView(R.id.tv_age)
    TextView tv_age;

    @BindView(R.id.iv_headUrl)
    RoundedImageView iv_headUrl;

    @BindView(R.id.system_msg)
    TextView system_msg;
    @BindView(R.id.note)
    TextView note;

    Unbinder unbinder;
    RequestOptions options;
    public UserInfo body;
    Login loginModel;
    private String noteStr = "今天还可抢10次红包，每天5次机会，分享任何帖子到朋友圈、好友可以加2次机会（只限当天用完）；塞红包加10次";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);

        unbinder = ButterKnife.bind(this, view);


        initViews();
        return view;
    }

    private void initViews() {
        loginModel = Hawk.get("LoginModel");
        options = new RequestOptions()
                .centerCrop();
        if(Hawk.contains("userInfo")){
            UserInfo userInfo = Hawk.get("userInfo");
            setView(userInfo);
        }
        userInfo();
    }

    private void userInfo() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {
                    @Override
                    public void onSuccess(UserInfo body) {
                        body.uid = loginModel.uid;
                        Hawk.put("userInfo", body);
                        MeFragment.this.body = body;
                        setView(body);
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

    private void setView(UserInfo body) {
        if(body.head_url_img != null && !StringUtils.isEmpty(body.head_url_img)){
            Glide.with(getActivity())
                    .load(body.head_url_img)
                    .apply(options)
                    .into(iv_headUrl);
        }else if(body.head_url != null && !body.head_url.isEmpty()){
            Glide.with(getActivity())
                    .load(body.head_url)
                    .apply(options)
                    .into(iv_headUrl);
        }

        tv_nickname.setText(StringUtils.isEmpty(body.nickname) ? "" : body.nickname);

        if(body.sex == 1){
            tv_sex.setText("男");
        }else if(body.sex == 2){
            tv_sex.setText("女");
        }

        StringBuilder city = new StringBuilder();
        city.append(StringUtils.isEmpty(body.provinces) ? "" : body.provinces).append
                (StringUtils.isEmpty(body.city) ? "" : body.city);
        tv_cty.setText(city.toString());

        tv_sign.setText(StringUtils.isEmpty(body.sign) ? "" : body.sign);

        tv_balance.setText(StringUtils.isEmpty(body.balance) ? "" : body.balance);

        tv_age.setText(body.age + "岁");
        if (body.system_msg != null && !body.system_msg.isEmpty()){
            system_msg.setText("\u3000\u3000" + body.system_msg);
        }else{
            system_msg.setText("");
        }
        //noteStr = "今天还可抢10次红包，每天5次机会，分享任何帖子到朋友圈、好友可以加2次机会（只限当天用完）；塞红包加10次";
        noteStr = "今天还可抢" + body.grad_red_num + "次红包，每天" + body.red_num + "次机会，分享任何帖子到朋友圈、好友可以加2次机会" +
                "（只限当天用完）；塞红包加" + body.post_red_num + "次";
        note.setText(noteStr);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            Login login = Hawk.get("LoginModel");
            if(login != null){
                userInfo();
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden){
            Login login = Hawk.get("LoginModel");
            if(login != null){
                userInfo();
            }
        }
    }

    @OnClick(R.id.iv_edit_profile)
    public void iv_edit_profile(){
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        intent.putExtra("userInfo", body);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.ll_my_publish)
    public void ll_my_publish(){
        Intent intent = new Intent(getActivity(), MyPublishActivity.class);
        intent.putExtra("uid", loginModel.uid + "");
        startActivity(intent);
    }

    @OnClick(R.id.ll_drafts)
    public void ll_drafts(){
        startActivity(new Intent(getActivity(), DraftsActivity.class));
    }

    @OnClick(R.id.ll_my_collect)
    public void ll_my_collect(){
        startActivity(new Intent(getActivity(), MyCollectActivity.class));
    }

    @OnClick(R.id.ll_my_footprint)
    public void ll_my_footprint(){
        startActivity(new Intent(getActivity(), MyFootprintActivity.class));
    }

    @OnClick(R.id.ll_setup)
    public void ll_setup(){
        startActivity(new Intent(getActivity(), SetUpActivity.class));
    }

    @OnClick(R.id.tv_detail)
    public void tv_detail(){
        startActivity(new Intent(getActivity(), DetailActivity.class));
    }

    @OnClick(R.id.tv_recharge)
    public void tv_recharge(){
        startActivity(new Intent(getActivity(), RechargeActivity.class));
    }

    @OnClick(R.id.tv_withdraw_cash)
    public void tv_withdraw_cash(){
        startActivity(new Intent(getActivity(), WithdrawCashActivity.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 200) {
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("userInfo");
            if(userInfo != null){
                setView(userInfo);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
