package com.yjyc.zhoubian.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtil {

    public PackageInfo getVersionName(Context context){
        try{
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            return packInfo;
        }catch(Exception e){
            return null;
        }
    }

}
