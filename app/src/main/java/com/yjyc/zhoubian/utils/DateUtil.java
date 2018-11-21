package com.yjyc.zhoubian.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

    /**
     * @param timill
     * */
    public static String converterDate(long timill){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = new Date(timill);
        return sdf.format(dt);
    }


    public static String getTimemess(long timeMilli) {
        long dsTimeMilli = System.currentTimeMillis() - timeMilli;
        long l;
        if (dsTimeMilli <= 24 * 60 * 60 * 1000) {
            String hours = converterDate(timeMilli).split(" ")[1];
            return hours.substring(0, (hours.length() - 3));
        } else {
            return converterDate(timeMilli);
        }
    }

    public static String getDate(){
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = new Date(System.currentTimeMillis());
        return sdf.format(dt);
    }

}
