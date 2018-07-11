package com.yuqian.mncommonlibrary.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.orhanobut.logger.Logger;
import com.yuqian.mncommonlibrary.R;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/19
 *     desc   : BaseFragmentDialog 编写新的Dialog需要继承它
 *     version: 1.0
 * </pre>
 */
public abstract class BaseFragmentDialog extends DialogFragment {

    public FragmentActivity mActivity;

    public boolean isShowing = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Window相关
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
            //动画
            int animations = initAnimations();
            if (animations != 0) {
                getDialog().getWindow().setWindowAnimations(animations);
            } else {
                //没有动画，默认系统
            }
        }
        //样式
        setStyle(R.style.CustomBaseFragmentDialog, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        //隐藏title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //点击外部不可取消
        getDialog().setCanceledOnTouchOutside(false);
        //拦截外部返回
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        //初始化其他可以覆盖上面
        init();
        return initView(inflater);
    }


    protected abstract View initView(LayoutInflater inflater);

    protected abstract int initAnimations();

    protected abstract void init();

    public float initBackgroundAlpha() {
        return 0.8f;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        isShowing = false;
    }

    @Override
    public void dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss();
        isShowing = false;
    }

    public void showDialog(FragmentActivity mActivity) {
        try {
            if (isShowing()) {
                return;
            }
            if (mActivity != null && mActivity.getSupportFragmentManager() != null) {
                this.mActivity = mActivity;
                FragmentManager supportFragmentManager = mActivity.getSupportFragmentManager();
                //在每个add事务前增加一个remove事务，防止连续的add
                supportFragmentManager.beginTransaction().remove(this).commit();
                show(supportFragmentManager, mActivity.getLocalClassName());
            }
        } catch (Exception e) {
            Logger.e("显示FragmentDialog异常：" + e.toString());
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        isShowing = true;
        super.show(manager, tag);
    }

    public boolean isShowing() {
        if ((isShowing) || (getDialog() != null && getDialog().isShowing())) {
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = initBackgroundAlpha();
            //设置宽度充满屏幕
            windowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            windowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(windowParams);
        }
    }

    @Override
    public void onDestroyView() {
        isShowing = false;
        super.onDestroyView();
    }

}
