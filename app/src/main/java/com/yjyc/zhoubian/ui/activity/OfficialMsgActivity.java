package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.OfficialMsgAdapter;
import com.yjyc.zhoubian.im.ECMIm;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.SiteMsgs;
import com.yjyc.zhoubian.model.SiteMsgsModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.view.MyClassicsHeader;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OfficialMsgActivity extends BaseActivity {

    @BindView(R.id.msg_list)
    RecyclerView messageList;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Login login;
    private List<SiteMsgs.SiteMsg> siteMsgs = new ArrayList<>();
    private OfficialMsgAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_msg);
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        if(login == null){
            startActivity(new Intent(this, LoginActivity.class));
            showToast("请先登录");
            finish();
            return;
        }
        initView();

    }

    private void initView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messageList.setLayoutManager(layoutManager);
        adapter = new OfficialMsgAdapter(this, siteMsgs);
        messageList.setAdapter(adapter);

        refreshLayout.setRefreshHeader(new MyClassicsHeader(this));
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.autoRefresh();
        ((MyClassicsHeader)refreshLayout.getRefreshHeader()).showLoadOnly();
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            adapter.clearData();
            loadMsgs();
        });
    }

    private void loadMsgs() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.SITEMSG)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<SiteMsgsModel, SiteMsgs>() {
                    @Override
                    public void onSuccess(SiteMsgs body) {
                        refreshLayout.finishRefresh();
                        if(body == null || body.site_msg == null || body.site_msg.size() <= 0){
                            return;
                        }
                        adapter.addDatas(body.site_msg);
                        ECMIm.getInstance().clearOfficialUndead();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        refreshLayout.finishRefresh();
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        finish();
                    }
                });
    }

    @OnClick({R.id.iv_left})
    public void click(View v){
        switch (v.getId()){
            case R.id.iv_left:
                finish();
                break;
        }
    };

}
