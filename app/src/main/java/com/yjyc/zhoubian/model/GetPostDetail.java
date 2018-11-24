package com.yjyc.zhoubian.model;

import java.util.List;

public class GetPostDetail {
    public Post post;
    public class Post{
        public int id;
        public int user_id;
        public int post_cate_id;
        public String custom_post_cate;
        public String title;
        public String body; //内容
        public List<String> pic;
        public float lon;
        public float lat;
        public String province;
        public String city;
        public int user_group_id; //用户身份id
        public String user_name;
        public int phone_from;
        public String phone;
        public int whether_open; //是否显示打电话
        public int price;
        public String price_unit;
        public String key_word;
        public int red_package_rule;
        public float red_package_money;
        public double single_red_money;
        public int red_package_number;
        public String red_package_password;
        public String rob_red_package_range;
        public int grad_red_package_number;
        public int view;
        public int virtual_distance;
        public String actual_title;
        public int refresh_number;
        public int weather_examine;
        public int admin_examine_status;
        public int delete_time;
        public String user_look_date_time;
        public String come_before;
        public String create_time;
        public String update_time;
        public String user_update_time;
        public String head_url;
        public boolean is_follow_user;
        public boolean is_collect;
    }
}
