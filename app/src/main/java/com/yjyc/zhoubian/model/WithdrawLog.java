package com.yjyc.zhoubian.model;

import java.util.List;

public class WithdrawLog {

    public List<WithdrawRecord> list;
    public boolean hasNextPages;

    public class WithdrawRecord{
        public int id;
        public String create_time;
        public int check_status;
        public double money;
    }
}
