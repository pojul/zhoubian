package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.RechargeRecordAdapter;
import com.yjyc.zhoubian.model.GetRechargeSetting;
import com.yjyc.zhoubian.model.GetRechargeSettingModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.RechargeLog;
import com.yjyc.zhoubian.model.RechargeLogModel;
import com.yjyc.zhoubian.pay.alipay.PayResult;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 充值记录
 * Created by Administrator on 2018/10/11/011.
 */

public class RechargeRecordActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    private static final int INIT = 34738;
    private Login login;
    private RechargeRecordAdapter myAdapter;
    private List<RechargeLog.RechargeRecord> records = new ArrayList<>();
    private int listRows = 10;
    private int currentPage = 1;
    private int loadDataFlag = 0; //0: refresh; 1: loadmore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
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
        initTitleBar("充值记录", v -> onBackPressed());
        myAdapter = new RechargeRecordAdapter(this, records);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            loadDataFlag = 1;
            reqRecords(false);
        });
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            myAdapter.clearDatas();
            loadDataFlag = 0;
            reqRecords(false);
        });
        reqRecords(true);
    }

    private void reqRecords(boolean showDialog) {
        int tempPage = 1;
        if(loadDataFlag == 0){
            currentPage = 1;
            myAdapter.clearDatas();
        }else{
            tempPage = currentPage + 1;
        }
        if(showDialog){
            LoadingDialog.showLoading(this);
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.RECHARGELOG)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("page", ("" + tempPage))
                .addParams("listRows", ("" + listRows))
                .execute(new AbsJsonCallBack<RechargeLogModel, RechargeLog>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(RechargeLog body) {
                        LoadingDialog.closeLoading();
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if(body == null || body.list == null || body.list.size() <= 0){
                            showToast("没有更多了");
                            return;
                        }
                        if(loadDataFlag == 1){
                            currentPage = currentPage + 1;
                        }
                        myAdapter.addData(body.list);
                    }
                });
    }

    private RechargeRecordActivity.MyHandler mHandler = new RechargeRecordActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<RechargeRecordActivity> activity;

        MyHandler(RechargeRecordActivity activity) {
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