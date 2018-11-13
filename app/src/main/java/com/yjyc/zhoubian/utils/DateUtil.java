package com.yjyc.zhoubian.utils;

import java.util.Calendar;

public class DateUtil {

    public static String getPostDetailDate(String baseDate){
        try{
            String[] dayHours = baseDate.split(" ");
            String[] days = dayHours[0].split("-");
            String[] hours = dayHours[1].split(":");
            Calendar c = Calendar.getInstance();
            String currentmYear = c.get(Calendar.YEAR) + ""; // 获取当前年份
            if(currentmYear.equals(days[0])){
                return (days[1] + "月" + days[2] + "日" + "  " + hours[0] + ":" + hours[1]);
            }else{
                return (days[0] + "年" + days[1] + "月" + days[2] + "日" + "  " + hours[0] + ":" + hours[1]);
            }
        }catch(Exception e){}
        return "";
    }

}
