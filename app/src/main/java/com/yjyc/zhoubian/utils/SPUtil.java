package com.yjyc.zhoubian.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor mEditor;
    private static SPUtil mSPUtil;
    public static String FIRST_LOGIN_IM = "firstLoginIm";

    public SPUtil(Context context) {
        mPreferences =  context.getSharedPreferences("SPUtil" ,Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
    }

    public static void Instance(Context context) {
        if (mSPUtil == null) {
            synchronized (SPUtil.class) {
                if (mSPUtil == null) {
                    mSPUtil = new SPUtil(context);
                }
            }
        }
    }

    public static SPUtil getInstance() {
        return mSPUtil;
    }

    public void putInt(String key,int value) {
        mEditor.putInt(key,value);
        mEditor.commit();
    }

    public int getInt(String key,int defaultValue) {
        return mPreferences.getInt(key, defaultValue);
    }

    public void putString(String key,String value) {
        mEditor.putString(key,value);
        mEditor.commit();
    }

    public String getString(String key) {
        return mPreferences.getString(key,"");
    }

    public void remove(String key) {
        mEditor.remove(key);
        mEditor.commit();
    }

}
