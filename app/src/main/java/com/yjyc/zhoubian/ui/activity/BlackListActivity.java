package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;

import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.BlackListAdapter;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCollection;
import com.yjyc.zhoubian.model.PostCollectionModel;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlackListActivity extends BaseActivity {

    @BindView(R.id.black_list_rv)
    RecyclerView blackListRv;

    private static final int INIT = 9567;
    private BlackListAdapter blackListAdapter;
    private List<BlackUser> datas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);
        ButterKnife.bind(this);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("黑名单", v -> onBackPressed());
        mHandler.sendEmptyMessageDelayed(INIT, 10);
    }

    private void init(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        blackListRv.setLayoutManager(layoutManager);
        blackListAdapter = new BlackListAdapter(this, datas);
        blackListRv.setAdapter(blackListAdapter);
        reqBlackList();
    }

    private void reqBlackList() {
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        LoadingDialog.showLoading(this);
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.BLACKLIST)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<BlackUserListModel, List<BlackUser>>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(List<BlackUser> body) {
                        LoadingDialog.closeLoading();
                        if(body == null || body.size() <= 0){
                            return;
                        }
                        blackListAdapter.addData(body);
                    }
                });
    }

    private BlackListActivity.MyHandler mHandler = new BlackListActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<BlackListActivity> fragment;
        MyHandler(BlackListActivity fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (fragment.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    fragment.get().init();
                    break;
            }
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_out_anim);
    }

}
