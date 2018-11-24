package com.yjyc.zhoubian.utils;

public class FileUtil {

    public static boolean isNetUrl(String url){
        if(url == null){
            return false;
        }
        if(url.toLowerCase().startsWith("http")) {
            return true;
        }
        return false;
    }

}
