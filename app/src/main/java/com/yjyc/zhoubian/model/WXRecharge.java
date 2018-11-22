package com.yjyc.zhoubian.model;

public class WXRecharge {
    public PayInfo pay_result;
    public class PayInfo{
        public String appid;
        public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
        public String sign;
    }
}
