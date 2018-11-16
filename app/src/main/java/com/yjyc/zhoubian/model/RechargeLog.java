package com.yjyc.zhoubian.model;

import java.util.List;

public class RechargeLog {
    public List<RechargeRecord> list;
    public class RechargeRecord{
        public int id;
        public String pay_time;
        public String money;
        public int pay_type;
        public int pay_status;
    }
    public boolean hasNextPages;
}
