package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
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
    CardAdapter myAdapter;
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
        initTitleBar("我的足迹", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myAdapter = new CardAdapter();

        myAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(mContext, PostDetailsActivity.class));
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {
                showPopWindow(iv_delete, isDown, position);
            }
        });
    }

    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
    }

    private PopupWindow popWindow;
    private int offsetX;
    private int offsetY;
    private void showPopWindow(ImageView iv_delete, boolean isDown, int[] location) {
        View contentView;
        if(isDown){
            contentView = LayoutInflater.from(this).inflate(R.layout.deletepopupdownlayout, null);

        }else {
            contentView = LayoutInflater.from(this).inflate(R.layout.deletepopuptoplayout, null);
        }
        RelativeLayout rl_dismiss = contentView.findViewById(R.id.rl_dismiss);
        rl_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });

        TextView tv_report = contentView.findViewById(R.id.tv_report);
        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                startActivity(new Intent(mContext, ReportActivity.class));
            }
        });

        popWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setContentView(contentView);
        contentView.measure(makeDropDownMeasureSpec(popWindow.getWidth()),
                makeDropDownMeasureSpec(popWindow.getHeight()));
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                rl_bg.setVisibility(View.GONE);
            }
        });

        //显示PopupWindow
        if(isDown){
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            popWindow.showAsDropDown(iv_delete);
        }else {
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            PopupWindowCompat.showAsDropDown(popWindow, iv_delete, offsetX, 0, Gravity.START);
        }
        rl_bg.setVisibility(View.VISIBLE);
    }


    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }
}