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

    public static String getFileType(String path){
        int index = path.lastIndexOf(".");
        if(index != -1){
            return path.substring((index + 1), path.length());
        }
        return "";
    }

    public static String getFileName(String path){
        int index = path.lastIndexOf("/");
        if(index == -1){
            index = 0;
        }
        return path.substring((index + 1) , path.length());
    }

}
