package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.yjyc.zhoubian.R;

import butterknife.ButterKnife;

/**
 * 提现
 * Created by Administrator on 2018/10/11/011.
 */

public class WithdrawCashActivity extends BaseActivity {
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("提现", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showRightPulishText();
        TextView tv = getRightPulishText();
        tv.setText("提现记录");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WithdrawCashActivity.this, RechargeRecordActivity.class));
            }
        });
    }

}