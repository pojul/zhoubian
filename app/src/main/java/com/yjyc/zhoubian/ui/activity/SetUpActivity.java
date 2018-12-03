package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.UpdateApp;
import com.yjyc.zhoubian.model.UpdateAppModel;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yjyc.zhoubian.utils.VersionUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class SetUpActivity extends BaseActivity {

    @BindView(R.id.black_list_ll)
    LinearLayout blackListLl;
    @BindView(R.id.nvestment_cooperation_ll)
    LinearLayout nvestmentCooperationLl;
    @BindView(R.id.clear_cache_ll)
    LinearLayout clearCacheLl;
    @BindView(R.id.check_update)
    LinearLayout checkUpdate;
    @BindView(R.id.exit_login)
    LinearLayout exitLogin;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_up);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }

    @OnClick({R.id.black_list_ll, R.id.nvestment_cooperation_ll, R.id.clear_cache_ll, R.id.check_update, R.id.exit_login})
    public void click(View v){
        switch (v.getId()){
            case R.id.black_list_ll:
                startActivityAni(BlackListActivity.class, null);
                break;
            case R.id.nvestment_cooperation_ll:
                startActivityAni(InvestCooperateActivity.class, null);
                break;
            case R.id.clear_cache_ll:
                break;
            case R.id.check_update:
                //reqVersionInfo();
                break;
            case R.id.exit_login:
                BaseApplication.getIntstance().loginOut();
                finish();
                break;
        }
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}