package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/16/016.
 */

public class FollowUsers {
    public List<FollowUsers.Data> list;
    public boolean hasNextPages;
    public class Data{
        public int uid;
        public int follow_user_id;
        public String create_time;
        public String nickname;
        public String head_url;
        public String sign;
    }
}
