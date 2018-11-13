package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/16/016.
 */

public class Fans {
    public List<Fans.Data> list;
    public boolean hasNextPages;
    public class Data{
        public int id;
        public int uid;
        public int type;
        public String money;
        public String balance;
        public String create_time;
        public String update_time;
        public String delete_time;

    }
}
