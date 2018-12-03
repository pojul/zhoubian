package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/30/030.
 */

public class SearchPosts {
    public List<SearchPost> list;
    public boolean hasNextPages;
    public class SearchPost{
        public int id;
        public int user_id;
        public int post_cate_id;
        public String custom_post_cate;
        public String post_cate_title;
        public String title;
        public String body;
        public String lon;
        public String lat;
        public String province;
        public String city;
        public int user_group_id;
        public String user_name;
        public int phone_from;
        public String phone;
        public int whether_open;
        public String price;
        public String price_unit;
        public String key_word;
        public int red_package_rule;
        public int red_package_number;
        public int view;
        public int weather_examine;
        public int admin_examine_status;
        public String delete_time;
        public String user_look_date_time;
        public String distance;
        public String time;
        public List<String> pic;
        public String actual_distance;
        private String create_time;
    }
}
