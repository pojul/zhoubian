package com.yuqian.mncommonlibrary.dialog;

import android.content.Context;

import com.maning.mndialoglibrary.MProgressDialog;
import com.maning.mndialoglibrary.config.MDialogConfig;
import com.maning.mndialoglibrary.listeners.OnDialogDismissListener;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/07/03
 *     desc   : 加载的Dialog
 *     version: 1.0
 * </pre>
 */
public class LoadingDialog {

    public static void showLoading(Context context) {
        if(!MProgressDialog.isShowing()){
            MProgressDialog.showProgress(context);
        }
    }

    public static void showLoading(Context context, String msg) {
        MProgressDialog.showProgress(context, msg);
    }

    public static void showLoading(Context context, boolean isCanceledOnTouchOutside, OnDialogDismissListener onDialogDismissListener) {
        showLoading(context, "加载中", isCanceledOnTouchOutside, onDialogDismissListener);
    }

    public static void showLoading(Context context, String msg, boolean isCanceledOnTouchOutside, OnDialogDismissListener onDialogDismissListener) {
        //自定义背景
        MDialogConfig mDialogConfig = new MDialogConfig.Builder()
                //点击外部是否可以取消
                .isCanceledOnTouchOutside(isCanceledOnTouchOutside)
                //消失监听
                .setOnDialogDismissListener(onDialogDismissListener)
                //构建
                .build();
        MProgressDialog.showProgress(context, msg, mDialogConfig);
    }

    /**
     * 完全自定义显示
     *
     * @param context
     * @param mDialogConfig
     */
    public static void showLoading(Context context, MDialogConfig mDialogConfig) {
        MProgressDialog.showProgress(context, mDialogConfig);
    }

    public static void closeLoading() {
        MProgressDialog.dismissProgress();
    }

}
