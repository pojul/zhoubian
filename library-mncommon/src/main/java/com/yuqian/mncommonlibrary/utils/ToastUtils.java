package com.yuqian.mncommonlibrary.utils;

import android.view.Gravity;


import com.yuqian.mncommonlibrary.MBaseManager;

import xyz.bboylin.universialtoast.UniversalToast;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/02/10
 *     desc   : Toast工具
 *     version: 1.0
 * </pre>
 */
public class ToastUtils {

    public static void show(String msg) {
        UniversalToast.makeText(MBaseManager.getApplication(), msg, UniversalToast.LENGTH_SHORT)
                .setGravity(Gravity.CENTER, 0, 0)
                .show();
    }

}
