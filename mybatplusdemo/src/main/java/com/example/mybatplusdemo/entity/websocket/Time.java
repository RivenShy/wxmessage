package com.example.mybatplusdemo.entity.websocket;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    private final static String timeFormat1 = "yyyy-MM-dd HH:mm:ss";
    private final static String timeFormat2 = "yyyy/MM/dd HH:mm:ss";
    private final static String timeFormat3 = "yyyy-MM-dd";
    private final static String timeFormat4 = "yyyy/MM/dd";


    /**
     * 获取当前时间
     * @return
     */
    public static String getTime(String timeFormat) {
        SimpleDateFormat df = new SimpleDateFormat(timeFormat);//设置日期格式
        return df.format(new Date());
    }

    /**
     * 获取当前时间默认时间格式
     * @return
     */
    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat(timeFormat1);
        return df.format(new Date());
    }


    /**
     * 字符串转Date
     * @param time
     * @param timeFormat
     * @return
     */
    public static Date strToDate(String time,String timeFormat)  {
        try {
            return new SimpleDateFormat(timeFormat).parse(time);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转Date（默认格式）
     * @param time
     * @return
     */
    public static Date strToDate(String time)  {
        try {
            return new SimpleDateFormat(timeFormat1).parse(time);
        }catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Date转字符串（默认格式）
     * @param time
     * @return
     */
    public static String dateToStr(Date time) {
        SimpleDateFormat df = new SimpleDateFormat(timeFormat1);
        return df.format(time);
    }

    /**
     * Date转字符串指定时间格式
     * @param time
     * @param timeFormat
     * @return
     */
    public static String dateToStr(Date time,String timeFormat) {
        SimpleDateFormat df = new SimpleDateFormat(timeFormat);
        return df.format(time);
    }

    /**
     * 获取时间戳
     * @return
     */
    public static long getTimeStamp() {
        //其他两种方式
        //long time3 = new Date().getTime();
        //long time1 = System.currentTimeMillis()；
        return Calendar.getInstance().getTimeInMillis();

    }

    /**
     *时间转化为时间戳
     * @param date
     * @return
     * @throws ParseException
     */
    public static long changeStamp(String date, String timeFormat) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        Date time= simpleDateFormat.parse(date);
        return time.getTime();
    }

    /**
     *时间转化为时间戳（默认格式）
     * @param date
     * @return
     * @throws ParseException
     */
    public static long changeStamp(String date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat1);
        Date time= simpleDateFormat.parse(date);
        return time.getTime();
    }


    /**
     *时间戳转化为时间
     * @param stamp
     * @return
     */
    public static String changeTime(String stamp, String timeFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat);
        Date date = new Date(Long.parseLong(stamp));
        return simpleDateFormat.format(date);
    }

    /**
     *时间戳转化为时间（默认格式）
     * @param stamp
     * @return
     */
    public static String changeTime(String stamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat1);
        Date date = new Date(Long.parseLong(stamp));
        return simpleDateFormat.format(date);
    }
}