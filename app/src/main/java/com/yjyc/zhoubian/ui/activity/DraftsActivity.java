package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.DraftsAdapter;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostDraft;
import com.yjyc.zhoubian.model.PostDraftModel;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 草稿箱
 * Created by Administrator on 2018/10/11/011.
 */

public class DraftsActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Context mContext;
    private int listRows = 10;
    private int page = 1;
    private Login login;
    private DraftsAdapter adapter;
    private int loadPostFlag; //0: refresh; 1: loadmore

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drafts);
        mContext = this;
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        if(login == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("草稿箱", v -> onBackPressed());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DraftsAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            loadPostFlag = 1;
            reqDrafts();
        });
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadPostFlag = 0;
            reqDrafts();
        });
        reqDrafts();
    }

    private void reqDrafts() {
        int tempPage = 1;
        if(loadPostFlag == 0){
            adapter.clearData();
            tempPage = 1;
            page = 1;
        }else{
            tempPage = page + 1;
        }
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.POSTDRAFT)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("listRows", ("" + listRows))
                .addParams("page", ("" + tempPage))
                .execute(new AbsJsonCallBack<PostDraftModel, PostDraft>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }

                    @Override
                    public void onSuccess(PostDraft body) {
                        LoadingDialog.closeLoading();
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if(body == null || body.list == null || body.list.size() <= 0){
                            return;
                        }
                        adapter.addData(body.list);
                        if(loadPostFlag != 0){
                            page = page + 1;
                        }
                    }
                });
    }


}