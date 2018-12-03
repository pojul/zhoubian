package com.yjyc.zhoubian.model;

public class UpdateApp {
    public AppInfo app_info;
    public class AppInfo{
        public int id;
        public String title;
        public int versionCode;
        public String versionName;
        public String downloadUrl;
    }
}
