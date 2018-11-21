package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.EvaluationExposeAdapter;
import com.yjyc.zhoubian.model.AcceptEvaluationExposeModel;
import com.yjyc.zhoubian.model.AcceptEvaluationExposes;
import com.yjyc.zhoubian.model.UserPostList;
import com.yjyc.zhoubian.model.UserPostListModel;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我收到的评价
 * Created by Administrator on 2018/10/10/010.
 */

@SuppressLint("ValidFragment")
public class MyEvaluationsFragment extends BaseFragment {

    private MyPublishActivity activity;
    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;
    Unbinder unbinder;
    public AcceptEvaluationExposes body;
    RequestOptions options;
    RequestOptions options2;
    private EvaluationExposeAdapter adapter;
    private List<AcceptEvaluationExposes.AcceptEvaluationExpose> datas = new ArrayList<>();
    public int page = 1;
    private int loadPostFlag = 1; // 0: loadmore; 1: refresh;

    public MyEvaluationsFragment(MyPublishActivity myPublishActivity) {
        activity = myPublishActivity;
    }

    public MyEvaluationsFragment(){
    }

    @Override
    View initView() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_publish, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        adapter = new EvaluationExposeAdapter(1, getActivity(), datas, 1);
        options = new RequestOptions()
                .centerCrop().placeholder( R.drawable.head_url).error(R.drawable.head_url);
        options2 = new RequestOptions()
                .centerCrop().placeholder( R.drawable.img_bg).error(R.drawable.img_bg);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(adapter);
    }

    @Override
    protected void Loading() {
        if (!isVisible){
            return;
        }

        if(activity != null && !ProgressDialog.isShowing()){
            ProgressDialog.showDialog(activity);
        }

        acceptEvaluationExpose();
    }

    public void acceptEvaluationExpose() {
        int reqpage = 1;
        if(loadPostFlag == 1){
            adapter.clearDatas();
        }else{
            reqpage = page + 1;
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.ACCEPTEVALUATIONEXPOSE)
                .addParams("uid", activity.uid)
                .addParams("listRows", "10")
                .addParams("cate_id", "1")
                .addParams("page", reqpage + "")
                .execute(new AbsJsonCallBack<AcceptEvaluationExposeModel, AcceptEvaluationExposes>() {
                    @Override
                    public void onSuccess(AcceptEvaluationExposes body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        MyEvaluationsFragment.this.body = body;
                        ArrayList<AcceptEvaluationExposes.AcceptEvaluationExpose> dataList = (ArrayList<AcceptEvaluationExposes.AcceptEvaluationExpose>) body.list;
                        if(loadPostFlag == 1){
                            page = 1;
                        }else{
                            page = page + 1;
                        }
                        adapter.addDatas(dataList);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                        if(activity.refreshLayout != null){
                            activity.refreshLayout.finishRefresh();
                            activity.refreshLayout.finishLoadmore();
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}