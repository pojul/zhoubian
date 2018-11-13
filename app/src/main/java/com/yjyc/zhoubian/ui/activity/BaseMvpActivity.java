package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;


/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/05
 *     desc   :BaseMvpActivity
 *     version: 1.0
 * </pre>
 */
public abstract class BaseMvpActivity extends MvpActivity {

    public Context mContext;

    public TextView tv_title;
    public LinearLayout btn_left;
    public RelativeLayout rl_title_bg;
    public TextView tv_right;
    public TextView tv_right_11sp;
    public LinearLayout btn_right;
    public ImageView iv_right;
    public ImageView iv_left;
    private TextView tv_right_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        BaseApplication.getIntstance().addActivity(this);

//        setDefaultStatusBar();
    }

//    public void setDefaultStatusBar() {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorStatusBarDefault), 0);
//    }
//
//    public void setLightStatusBar() {
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorStatusBarWhite), 20);
//    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        BaseApplication.getIntstance().removeActivity(this);
        hideLoading();
        super.onDestroy();
    }

    /**
     * 在子类中初始化对应的presenter
     *
     * @return 相应的presenter
     */

    public void showLoading() {
        showLoading("");
    }

    public void showLoading(String msg) {
        ProgressDialog.showDialog(this, msg);
    }

    public void hideLoading() {
        ProgressDialog.dismiss();
    }


    public void hideLeftButton() {
        btn_left = (LinearLayout) findViewById(R.id.btn_left);
        if (btn_left != null) {
            btn_left.setVisibility(View.GONE);
        }
    }

    public ImageView getLeftImageView() {
        iv_left = (ImageView) findViewById(R.id.iv_left);
        return iv_left;
    }

    public void showLeftButton() {
        btn_left = (LinearLayout) findViewById(R.id.btn_left);
        if (btn_left != null) {
            btn_left.setVisibility(View.VISIBLE);
        }
    }

    public void showRightText() {
        tv_right = (TextView) findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setVisibility(View.VISIBLE);
        }
    }

    public void showRightPulishText() {
        tv_right_publish = (TextView) findViewById(R.id.tv_right_publish);
        if (tv_right_publish != null) {
            tv_right_publish.setVisibility(View.VISIBLE);
        }
    }

    public TextView getRightPulishText() {
        return tv_right_publish ;
    }

    public void showRightText_11sp() {
        tv_right_11sp = (TextView) findViewById(R.id.tv_right_11sp);
        if (tv_right_11sp != null) {
            tv_right_11sp.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightText() {
        tv_right = (TextView) findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setVisibility(View.GONE);
        }
    }

    public void setRightText(String title, View.OnClickListener onClickListener) {
        tv_right = (TextView) findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setText(title);
            if (onClickListener != null) {
                tv_right.setOnClickListener(onClickListener);
            }
        }
    }

    public void setRightText_11sp(String title, View.OnClickListener onClickListener) {
        tv_right_11sp = (TextView) findViewById(R.id.tv_right_11sp);
        if (tv_right_11sp != null) {
            tv_right_11sp.setText(title);
            if (onClickListener != null) {
                tv_right_11sp.setOnClickListener(onClickListener);
            }
        }
    }

    public void setRightTextColor(@ColorInt int color) {
        tv_right = (TextView) findViewById(R.id.tv_right);
        if (tv_right != null) {
            tv_right.setTextColor(color);
        }
    }

    public void setRightTextColor_11sp(@ColorInt int color) {
        tv_right_11sp = (TextView) findViewById(R.id.tv_right_11sp);
        if (tv_right_11sp != null) {
            tv_right_11sp.setTextColor(color);
        }
    }

    public void showRightImage() {
        btn_right = (LinearLayout) findViewById(R.id.btn_right);
        if (btn_right != null) {
            btn_right.setVisibility(View.VISIBLE);
        }
    }

    public void hideRightImage() {
        btn_right = (LinearLayout) findViewById(R.id.btn_right);
        if (btn_right != null) {
            btn_right.setVisibility(View.GONE);
        }
    }

    public void setRightImage(@DrawableRes int resId, View.OnClickListener onClickListener) {
        iv_right = (ImageView) findViewById(R.id.iv_right);
        if (iv_right != null) {
            iv_right.setImageResource(resId);
            if (onClickListener != null) {
                iv_right.setOnClickListener(onClickListener);
            }
        }
    }

    public void setBackgroundColor(@ColorInt int backgroundColor) {
        rl_title_bg = (RelativeLayout) findViewById(R.id.rl_title_bg);
        if (rl_title_bg != null) {
            rl_title_bg.setBackgroundColor(backgroundColor);
        }
    }

    public void initTitleBar(String title) {
        initTitleBar(title, null);
    }
    public void setTitleColor(@ColorInt int titleColor) {
        tv_title = (TextView) findViewById(R.id.tv_title);
        if(tv_title != null){
            tv_title.setTextColor(titleColor);
        }
    }

    public void initTitleBar(String title, View.OnClickListener onClickListener) {
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_left = (LinearLayout) findViewById(R.id.btn_left);
        if (tv_title != null) {
            tv_title.setText(title);
        }
        if (btn_left != null) {
            if (onClickListener == null) {
                btn_left.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            } else {
                btn_left.setOnClickListener(onClickListener);
            }
        }
    }

    //失败显示页面
    public LinearLayout ll_fail_view;
    public TextView tv_reload;

//    public void showErrorView(View.OnClickListener onClickListener) {
//        ll_fail_view = (LinearLayout) findViewById(R.id.ll_fail_view);
//        tv_reload = (TextView) findViewById(R.id.tv_reload);
//        if (ll_fail_view != null && tv_reload != null) {
//            ll_fail_view.setVisibility(View.VISIBLE);
//            tv_reload.setOnClickListener(onClickListener);
//        }
//    }
//
//    public void hideErrorView() {
//        ll_fail_view = (LinearLayout) findViewById(R.id.ll_fail_view);
//        if (ll_fail_view != null) {
//            ll_fail_view.setVisibility(View.GONE);
//        }
//    }

}