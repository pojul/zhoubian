package com.yjyc.zhoubian.model;

import java.util.List;

public class ExperienceDetail {
    public Detail detail;
    public class Detail{
        public int id;
        public int uid;
        public int pid;
        public int view;
        public String title;
        public String body;
        public String create_time;
        public String custom_cate;
        public List<String> pic;
        public boolean is_follow_user;
    }
    public User user;
    public class User{
        public String nickname;
        public String head_url;
        public String login_time;
        public String phone;
        public String login_interval_time;
    }
}
