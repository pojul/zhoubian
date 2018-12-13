package com.yjyc.zhoubian.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.blankj.utilcode.util.StringUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.ExperienceAdapter;
import com.yjyc.zhoubian.model.ExperienceList;
import com.yjyc.zhoubian.model.ExperienceListModel;
import com.yjyc.zhoubian.model.ExperienceSave;
import com.yjyc.zhoubian.model.ExperienceSaveModel;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.activity.PublishValuableBookActivity;
import com.yjyc.zhoubian.ui.activity.ValuableBookDetailActivity;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 宝典
 * Created by Administrator on 2018/10/9/009.
 */

public class ValuableBookFragment extends Fragment{

    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    public RefreshLayout refreshLayout;

    Unbinder unbinder;

    private int listRows = 10;
    private int page = 1;
    private ExperienceAdapter adapter;
    private List<ExperienceList.Experience> experiences = new ArrayList<>();
    private int loadPostFlag = 1;
    public boolean init;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valuable_book, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局
        recyclerview.setLayoutManager(layoutManager);
        adapter = new ExperienceAdapter(experiences, getActivity());
        recyclerview.setAdapter(adapter);

        refreshLayout.setEnableLoadmore(false);
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            loadPostFlag = 0;
            reqExperience(false);
        });
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            adapter.clearDatas();
            loadPostFlag = 1;
            reqExperience(false);
        });
        reqExperience(true);
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    if(lastVisiblePosition >= layoutManager.getItemCount() - 1){
                        loadPostFlag = 0;
                        reqExperience(false);
                    }
                }
            }
        });
    }

    private void reqExperience(boolean showDialog) {
        int tempPage = 1;
        if(loadPostFlag == 0){
            tempPage = page + 1;
        }else if(loadPostFlag == 1){
            page = 1;
        }
        /*if(showDialog){
            LoadingDialog.showLoading(getActivity());
        }*/
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.EXPERIENCELIST)
                .addParams("page", ("" + tempPage))
                .addParams("listRows", ("" + listRows))
                .execute(new AbsJsonCallBack<ExperienceListModel, ExperienceList>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        //LoadingDialog.closeLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        com.yuqian.mncommonlibrary.utils.ToastUtils.show(StringUtils.isEmpty(errorMsg) ? errorMsg : errorMsg);
                    }

                    @Override
                    public void onSuccess(ExperienceList body) {
                        //LoadingDialog.closeLoading();
                        refreshLayout.finishRefresh();
                        refreshLayout.finishLoadmore();
                        adapter.addDatas(body.list);
                        if(page == 1){
                            new Handler(Looper.getMainLooper()).postDelayed(()->{
                                recyclerview.smoothScrollToPosition(0);
                            }, 600);
                        }
                        if(loadPostFlag == 0){
                            page = page + 1;
                        }
                    }
                });
    }

    @OnClick(R.id.tv_publish_valuable_book)
    public void tv_publish_valuable_book(){
        startActivity(new Intent(getActivity(), PublishValuableBookActivity.class));
    }

}