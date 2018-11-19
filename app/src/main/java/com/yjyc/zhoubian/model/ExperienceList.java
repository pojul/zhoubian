package com.yjyc.zhoubian.model;

import java.util.List;

public class ExperienceList {
    public List<Experience> list;
    public boolean hasNextPages;
    public class Experience {
        public int id;
        public int pid;
        public String title;
        public String body;
        public String create_time;
        public List<String> pic;
        public int uid;
        public int view;
        public String cate_name;
        public String user_name;
        public String time;
    }
}
