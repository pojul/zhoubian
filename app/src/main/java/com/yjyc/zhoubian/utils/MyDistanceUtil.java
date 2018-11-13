package com.yjyc.zhoubian.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MyDistanceUtil {

    public static String getDisttanceStr(double distance){
        if(distance < 1000){
            String distanceStr = new BigDecimal(("" + distance)).setScale(1, RoundingMode.HALF_UP).toString();
            return distanceStr + "m";
        }else{
            String distanceKmStr = new BigDecimal(("" + distance * 0.001)).setScale(1, RoundingMode.HALF_UP).toString();
            return distanceKmStr + "KM";
        }
    }

}
