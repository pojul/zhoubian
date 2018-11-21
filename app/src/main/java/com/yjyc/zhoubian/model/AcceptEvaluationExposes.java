package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/18/018.
 */

public class AcceptEvaluationExposes {
    public List<AcceptEvaluationExpose> list;
    public boolean hasNextPages;
    public class AcceptEvaluationExpose{
        public int id;
        public int cate_id;
        public int user_id;
        public String phone;
        public int be_exposed_user_id;
        public String be_exposed_user_phone;
        public String body;
        public List<String> pic;
        public int admin_examine_status;
        public String delete_time;
        public String create_time;
        public String update_time;
        public UserInfo user_info;
        public UserInfo be_user_info;
        public UserReply user_reply;
    }
    public class UserReply{
        public int id;
        public String create_time;
        public String body;
    }
}
