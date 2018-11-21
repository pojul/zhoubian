package com.yjyc.zhoubian.model;

public class WXRecharge {
    public PayInfo pay_result;
    public class PayInfo{
        public String appId;
        public String timeStamp;
        public String nonceStr;
        public String prepayId;
        public String signType;
        public String paySign;
        public String partnerId;
    }
}
