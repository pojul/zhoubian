package com.yjyc.zhoubian.model;

import java.util.List;

public class SiteMsgs {
    public List<SiteMsg> site_msg;
    public class SiteMsg{
        public int id;
        public String title;
        public String body;
        public String pic;
        public String create_time;
    }
}
