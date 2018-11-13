package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/18/018.
 */

public class UserGroups {
    public List<UserGroup> list;
    public class UserGroup{
        public int id;
        public String title;
        public int isChecked;
    }
}
