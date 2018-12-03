package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.CardAdapter;
import com.yjyc.zhoubian.adapter.InterestPostAdapter;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.ui.view.SwipeBackLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class MyFootprintActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.rl_bg)
    public RelativeLayout rl_bg;
    private Context mContext;
    private InterestPostAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_footprint);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("我的足迹", v -> onBackPressed());

    }

    private void initDate(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        adapter = new InterestPostAdapter(BaseApplication.getIntstance().viewedPost, this);
        recyclerView.setAdapter(adapter);
    }
}