package com.yjyc.zhoubian.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yjyc.zhoubian.R;


/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/16
 *     desc   : 加载进度
 *     version: 1.0
 * </pre>
 */
public class ProgressDialog {

    private static Dialog mDialog;

    public static void showDialog(Context context) {
        showDialog(context, null);
    }

    public static void showDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View mProgressDialogView = inflater.inflate(R.layout.dialog_progress, null);// 得到加载view
        mDialog = new Dialog(context, R.style.CustomDialog);// 创建自定义样式dialog
        mDialog.setCancelable(false);// 不可以用“返回键”取消
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setContentView(mProgressDialogView);// 设置布局

        //设置整个Dialog的宽高
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = ((Activity) context).getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;
        int screenH = dm.heightPixels;

        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.width = screenW;
        layoutParams.height = screenH;
        mDialog.getWindow().setAttributes(layoutParams);

        TextView tv_progress_msg = (TextView) mProgressDialogView.findViewById(R.id.tv_progress_msg);
        if (!TextUtils.isEmpty(msg)) {
            tv_progress_msg.setText(msg);
        } else {
            tv_progress_msg.setText("加载中...");
        }
        mDialog.show();
    }

    public static boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        } else {
            return false;
        }
    }

    public static void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
