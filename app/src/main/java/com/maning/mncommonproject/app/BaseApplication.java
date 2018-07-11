package com.maning.mncommonproject.app;

import android.app.Application;
import android.os.Handler;

import com.yuqian.mncommonlibrary.MBaseManager;

/**
 * @author : maning
 * @desc :
 */
public class BaseApplication extends Application {

    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        //初始化
        MBaseManager.init(this, "---logtag---", true);

    }

    public static Handler getHandler() {
        return mHandler;
    }
}
