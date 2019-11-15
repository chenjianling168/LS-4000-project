package com.example.chen.ls4000.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/3 0003.
 */

public class TimeUtils {

    public static final String PATTERN_STANDARD08W = "yyyyMMdd";
    public static final String PATTERN_STANDARD12W = "yyyyMMddHHmm";
    public static final String PATTERN_STANDARD14W = "yyyyMMddHHmmss";
    public static final String PATTERN_STANDARD17W = "yyyyMMddHHmmssSSS";

    public static final String PATTERN_STANDARD10H = "yyyy-MM-dd";
    public static final String PATTERN_STANDARD16H = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_STANDARD19H = "yyyy-MM-dd HH:mm:ss";

    public static final String PATTERN_STANDARD10X = "yyyy/MM/dd";
    public static final String PATTERN_STANDARD16X = "yyyy/MM/dd HH:mm";
    public static final String PATTERN_STANDARD19X = "yyyy/MM/dd HH:mm:ss";




    /**
     * 获取当前时间，精确到秒
     * @return
     */
    public static String getCurTime(){
        SimpleDateFormat simpleDateFormatt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = simpleDateFormatt.format(curDate);
        return curTime;
    }


    /**
     * 获取当前时间格式为 yy.MM.dd
     */
    public static String getCur2Time(){
        SimpleDateFormat simpleDateFormatt = new SimpleDateFormat("yy.MM.dd");
        Date curDate = new Date(System.currentTimeMillis());
        String cur2Time = simpleDateFormatt.format(curDate);
        return cur2Time;

    }

    //时间排序
    public static Date stringToDate(String dateString) {
        ParsePosition position = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date dateValue = simpleDateFormat.parse(dateString, position);
        return dateValue;
    }




    /**
     * 获取当前时间，精确到秒
     * @return
     */
    public static Long getTimeSecond(String ss){
        DateFormat simpleDateFormatt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        long curTime = 0;
        try {
            curTime = simpleDateFormatt.parse(ss).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return curTime;
    }

    /**
     * 获取当前时间，精确到小时
     * @return
     */
    public static String getCurTimeHour(){
        SimpleDateFormat simpleDateFormatt = new SimpleDateFormat("yyyy-MM-dd HH");
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = simpleDateFormatt.format(curDate);
        return curTime;
    }

    /**
     * 获取当前时间，精确到分钟
     * @return
     */
    public static String getCurTimeMinute(){
        SimpleDateFormat simpleDateFormatt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = simpleDateFormatt.format(curDate);
        return curTime;
    }

    public static long millionTime(String str){
        // String str = "201104141302";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");

        long millionSeconds = 0;//毫秒
        try {
            millionSeconds = sdf.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return millionSeconds;
    }

    public static boolean getIfTime(String time){
        boolean flag = true;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long second = format.parse(time).getTime();
            if(second> System.currentTimeMillis()){
                flag = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public static boolean dateBool(String strEnd) {
        boolean dateBool = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date dt1 = null;
        Date dt2 = new Date((System.currentTimeMillis()));
        try {
            dt1 = sdf.parse(strEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(dt2.getTime()<= dt1.getTime()){
            dateBool = true;
        }else{
            dateBool = false;
        }
        return dateBool;
    }

    public static int overdueBool(String bornTime,String shelflife) {
        int dateBool = -1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt1 = null;
        Date dt2 = new Date((System.currentTimeMillis()));
        try {
            dt1 = sdf.parse(bornTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ss = dt2.getTime()-dt1.getTime();
        if(ss>=0){
            long s =  (ss)/ 1000 / 60/ 60 / 24 / 30;//int s = (int)((dt2.getTime()-dt1.getTime()) / 1000 / 60 / 60 / 24 / 30);
            if( s <= Integer.valueOf(shelflife)){
                dateBool = 0;
            }else{
                dateBool = -1;
            }
        }else{
            dateBool = 1;
        }

        return dateBool;
    }

    public static boolean overdueBool(String bornTime) {
        boolean dateBool = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date dt1 = null;
        Date dt2 = new Date((System.currentTimeMillis()));
        try {
            dt1 = sdf.parse(bornTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ss = dt2.getTime()-dt1.getTime();
        if(ss>=0){
                dateBool = true;
        }else{
            dateBool = false;
        }

        return dateBool;
    }

    public static String getSeleTime(String time){
        String t = "";
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            Date d1=new Date(time);
            t=format.format(d1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }
    /**
     * @Title: date2String
     * @Description: 日期格式的时间转化成字符串格式的时间
     * @author YFB
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            throw new java.lang.IllegalArgumentException("timestamp null illegal");
        }
        pattern = (pattern == null || pattern.equals(""))?PATTERN_STANDARD19H:pattern;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * @Title: string2Date
     * @Description: 字符串格式的时间转化成日期格式的时间
     * @author YFB
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date string2Date(String strDate, String pattern) {
        if (strDate == null || strDate.equals("")) {
            throw new RuntimeException("strDate is null");
        }
        pattern = (pattern == null || pattern.equals(""))?PATTERN_STANDARD19H:pattern;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = sdf.parse(strDate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * @Title: getCurrentTime
     * @Description: 取得当前系统时间
     * @author YFB
     * @param format 格式 17位(yyyyMMddHHmmssSSS) (14位:yyyyMMddHHmmss) (12位:yyyyMMddHHmm) (8位:yyyyMMdd)
     * @return
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat formatDate = new SimpleDateFormat(format);
        String date = formatDate.format(new Date());
        return date;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * @Title: getWantDate
     * @Description: 获取想要的时间格式
     * @author YFB
     * @param dateStr
     * @param wantFormat
     * @return
     */
    public static String getWantDate(String dateStr,String wantFormat){
        if(!"".equals(dateStr)&&dateStr!=null){
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch(len){
                case 8:pattern = PATTERN_STANDARD08W;break;
                case 12:pattern = PATTERN_STANDARD12W;break;
                case 14:pattern = PATTERN_STANDARD14W;break;
                case 17:pattern = PATTERN_STANDARD17W;break;
                case 10:pattern = (dateStr.contains("-"))?PATTERN_STANDARD10H:PATTERN_STANDARD10X;break;
                case 16:pattern = (dateStr.contains("-"))?PATTERN_STANDARD16H:PATTERN_STANDARD16X;break;
                case 19:pattern = (dateStr.contains("-"))?PATTERN_STANDARD19H:PATTERN_STANDARD19X;break;
                default:pattern = PATTERN_STANDARD14W;break;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(wantFormat);
            try {
                SimpleDateFormat sdfStr = new SimpleDateFormat(pattern);
                Date date = sdfStr.parse(dateStr);
                dateStr = sdf.format(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dateStr;
    }

    /**
     * @Title: getAfterTime
     * @Description: 获取该时间的几分钟之后的时间
     * @author YFB
     * @param dateStr
     * @param minute
     * @return
     */
    public static String getAfterTime(String dateStr,int minute){
        String returnStr = "";
        try {
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch(len){
                case 8:pattern = PATTERN_STANDARD08W;break;
                case 10:pattern = PATTERN_STANDARD10H;break;
                case 12:pattern = PATTERN_STANDARD12W;break;
                case 14:pattern = PATTERN_STANDARD14W;break;
                case 16:pattern = PATTERN_STANDARD16H;break;
                case 17:pattern = PATTERN_STANDARD17W;break;
                case 19:pattern = PATTERN_STANDARD19H;break;
                default:pattern = PATTERN_STANDARD14W;break;
            }
            SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
            Date date = null;
            date = formatDate.parse(dateStr);
            Date afterDate = new Date(date.getTime()+(60000*minute));
            returnStr = formatDate.format(afterDate);
        } catch (Exception e) {
            returnStr = dateStr;
            e.printStackTrace();
        }
        return returnStr;
    }

    /**
     * @Title: getBeforeTime
     * @Description: 获取该时间的几分钟之前的时间
     * @author YFB
     * @param dateStr
     * @param minute
     * @return
     */
    public static String getBeforeTime(String dateStr,int minute){
        String returnStr = "";
        try {
            String pattern = PATTERN_STANDARD14W;
            int len = dateStr.length();
            switch(len){
                case 8:pattern = PATTERN_STANDARD08W;break;
                case 10:pattern = PATTERN_STANDARD10H;break;
                case 12:pattern = PATTERN_STANDARD12W;break;
                case 14:pattern = PATTERN_STANDARD14W;break;
                case 16:pattern = PATTERN_STANDARD16H;break;
                case 17:pattern = PATTERN_STANDARD17W;break;
                case 19:pattern = PATTERN_STANDARD19H;break;
                default:pattern = PATTERN_STANDARD14W;break;
            }
            SimpleDateFormat formatDate = new SimpleDateFormat(pattern);
            Date date = null;
            date = formatDate.parse(dateStr);
            Date afterDate = new Date(date.getTime()-(60000*minute));
            returnStr = formatDate.format(afterDate);
        } catch (Exception e) {
            returnStr = dateStr;
            e.printStackTrace();
        }
        return returnStr;
    }


}
