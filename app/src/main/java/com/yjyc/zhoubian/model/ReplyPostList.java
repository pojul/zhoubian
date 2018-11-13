package com.yjyc.zhoubian.model;

import java.util.List;

public class ReplyPostList {

    public class ReplyPost{
        public int id;
        public int reply_user_id;
        public int article_id;
        public int pid;
        public String nickname;
        public String head_url;
        public String body;
        public String create_time;
        public String grab_red_package_msg;
        public String interval_time;
        public int _level;
        public String _html;
        public List<ReplyPost> _data;
        public int oneLevelUid;
        public int oneLevelId;
        public String oneLevelNickName;
    }

}
