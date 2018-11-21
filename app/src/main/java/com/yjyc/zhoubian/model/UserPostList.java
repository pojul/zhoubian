package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/18/018.
 */

public class UserPostList {
    public List<UserPost> list;
    public boolean hasNextPages;
    public class UserPost{
        public int id;
        public int uid;
        public String user_name;
        public String title;
        public String lon;
        public String lat;
        public String create_time;
        public String price;
        public int red_package_rule;    //0不属于红包推广 1属于
        public String time;
        public List<String> pic;
        public int view;
        public int refresh_number;
    }
}
