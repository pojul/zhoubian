package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/25/025.
 */

public class PostCollectionLists {
    public List<PostCollectionLists.Data> list;
    public boolean hasNextPages;
    public class Data{
        public int uid;
        public int post_id;
        public String create_time;
        public String post_status_msg;
        public PostInfo post_info;
    }

    public class PostInfo{
        public int uid;
        public String user_name;
        public String title;
        public String custom_post_cate;
        public String post_cate_title;
        public List<String> pic;
        public String lon;
        public String lat;
        public String create_time;
        public String interval_time;
        public String distance;
        public int view;
        public int red_package_rule;
    }
}
