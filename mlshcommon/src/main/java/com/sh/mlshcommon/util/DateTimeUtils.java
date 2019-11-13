package com.sh.mlshcommon.util;

import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA
 * User：黄飞飞
 * Date：2016/5/6 10:23
 * Description：
 */
public class DateTimeUtils {

    public static final String SIMPLE_DATE = "yyyy-MM-dd";
    public static final String SIMPLE_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String COMPACT_DATE = "yyyyMMdd";
    public static final String COMPACT_DATE_TIME = "yyyyMMddHHmmss";
    public static final String CHINA_DATE_SHORT_FORMAT = "yyyy年MM月dd日";
    public static final String WEEK_DAY_FORMAT = "EEEE";

    /**
     * 将时间转化成标准的yyyy-MM-dd HH:mm:ss格式
     * 如果为空返回空字符串
     * @param date
     * @return 格式为yyyy-MM-dd HH:mm:ss的string串
     */
    public static String toSimpleDateTimeString(Date date) {
        if(date == null){
            return new String();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_TIME);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将时间转化成标准的yyyyMMdd格式
     * 如果为空返回空字符串
     * @param date
     * @return 格式为yyyyMMdd的string串
     */
    public static String toCompactDateString(Date date) {
        if(date == null){
            return new String();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(COMPACT_DATE);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将时间转化成标准的yyyyMMddHHmmss格式
     * 如果为空返回空字符串
     * @param date
     * @return 格式为yyyyMMddHHmmss的string串
     */
    public static String toCompactDateTimeString(Date date) {
        if(date == null){
            return new String();
        }
        SimpleDateFormat formatter = new SimpleDateFormat(COMPACT_DATE_TIME);
        String dateString = formatter.format(date);
        return dateString;
    }
    /**
     * 将时间转化成标准的yyyy-MM-dd格式
     * 如果为空,返回空字符串
     * @param date
     * @return 格式为yyyy-MM-dd的string串
     */
    public static String toSimpleDateString(Date date) {
        if(date == null){
            return new String();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将时间转化成yyyy年MM月dd日格式
     * 如果为空,返回空字符串
     * @param date
     * @return 格式为yyyy年MM月dd日的string串
     */
    public static String toChinaShortDateString(Date date) {
        if(date == null){
            return new String();
        }

        SimpleDateFormat formatter = new SimpleDateFormat(CHINA_DATE_SHORT_FORMAT);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将时间戳转化成标准的yyyy-MM-dd HH:mm:ss格式
     * 如果为空返回空字符串
     * @param timeStamp 10位或13位
     * @return 格式为yyyy-MM-dd HH:mm:ss的string串
     */
    public static String toSimpleDateTimeString(Long timeStamp) {
        Date date = getDateByTimeStamp(timeStamp);
        if(date == null){
            return "";
        }
        return toSimpleDateTimeString(date);
    }

    /**
     * 将时间转化成标准的yyyyMMdd格式
     * 如果为空返回空字符串
     * @param timeStamp 10位或13位
     * @return 格式为yyyyMMdd的string串
     */
    public static String toCompactDateString(Long timeStamp) {
        Date date = getDateByTimeStamp(timeStamp);
        if(date == null){
            return "";
        }
        return toCompactDateString(date);
    }

    /**
     * 将时间转化成标准的yyyyMMddHHmmss格式
     * 如果为空返回空字符串
     * @param timeStamp 10位或13位
     * @return 格式为yyyyMMddHHmmss的string串
     */
    public static String toCompactDateTimeString(Long timeStamp) {
        Date date = getDateByTimeStamp(timeStamp);
        if(date == null){
            return "";
        }
        return toCompactDateTimeString(date);
    }
    /**
     * 将时间转化成标准的yyyy-MM-dd格式
     * 如果为空,返回空字符串
     * @param timeStamp 10位或13位
     * @return 格式为yyyy-MM-dd的string串
     */
    public static String toSimpleDateString(Long timeStamp) {
        Date date = getDateByTimeStamp(timeStamp);
        if(date == null){
            return "";
        }
        return toSimpleDateString(date);
    }

    /**
     * 将时间戳转化成yyyy年MM月dd日格式
     * 如果为空,返回空字符串
     * @param timeStamp 10位或13位
     * @return 格式为yyyy年MM月dd日的string串
     */
    public static String toChinaShortDateString(Long timeStamp) {
        Date date = getDateByTimeStamp(timeStamp);
        if(date == null){
            return "";
        }
        return toChinaShortDateString(date);
    }

    /**
     * 将时间转化成标准的yyyy-MM-dd 00:00:00格式
     * 如果为空,返回空字符串
     * @param date
     * @return 格式为yyyy-MM-dd 00:00:00的string串
     */
    public static String getDayBeginTimeString(Date date) {
        if(date == null){
            return new String();
        }

        return new SimpleDateFormat(SIMPLE_DATE).format(date) + " 00:00:00";
    }

    /**
     * 获取当前时间
     * @return 格式为yyyy-MM-dd HH:mm:ss的string串
     */
    public static String toCurrentSimpleDateTimeString(){
        return toSimpleDateTimeString(new Date());
    }

    /**
     * 获取当前时间
     * @return 格式为yyyy-MM-dd的string串
     */
    public static String toCurrentSimpleDateString(){
        return toSimpleDateString(new Date());
    }

    /**
     * 获取当前时间
     * @return 格式为yyyyMMdd的string串
     */
    public static String toCurrentCompactDateString(){
        return toCompactDateString(new Date());
    }

    /**
     * 获取当前时间
     * @return 格式为yyyyMMddHHmmss的string串
     */
    public static String toCurrentCompactDateTimeString(){
        return toCompactDateTimeString(new Date());
    }

    /**
     * 格式yyyy-MM-dd HH:mm:ss字符串转换为Date
     * @param simpleDateTimeString
     * @return
     */
    public static Date fromSimpleDateTimeString(String simpleDateTimeString) {
        if (StringUtils.isBlank(simpleDateTimeString)){
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_TIME);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = null;
        //先以标准格式转换,如果异常,则以短格式转换 ,均异常则报错,返回空
        try {
            strtodate = formatter.parse(simpleDateTimeString, pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }

    /**
     * 格式yyyy-MM-dd字符串转换为Date
     * @param simpleDateString
     * @return
     */
    public static Date fromSimpleDateString(String simpleDateString) {
        if (StringUtils.isBlank(simpleDateString)){
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = null;
        //先以标准格式转换,如果异常,则以短格式转换 ,均异常则报错,返回空
        try {
            strtodate = formatter.parse(simpleDateString, pos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }

    /**
     * 格式yyyyMMddHHmmss字符串转化为Date
     * @param compactDateTimeString
     * @return
     */
    public static Date fromCompactDateTimeString(String compactDateTimeString) {
        if (StringUtils.isBlank(compactDateTimeString)){
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(COMPACT_DATE_TIME);
        Date strtodate = null;
        //先以标准格式转换,如果异常,则以短格式转换 ,均异常则报错,返回空
        try {
            strtodate = formatter.parse(compactDateTimeString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }

    /**
     * 格式yyyyMMdd字符串转化为Date
     * @param compactDateString
     * @return
     */
    public static Date fromCompactDateString(String compactDateString) {
        if (StringUtils.isBlank(compactDateString)){
            return null;
        }

        SimpleDateFormat formatter = new SimpleDateFormat(COMPACT_DATE);
        Date strtodate = null;
        //先以标准格式转换,如果异常,则以短格式转换 ,均异常则报错,返回空
        try {
            strtodate = formatter.parse(compactDateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strtodate;
    }


    /**
     * 计算两个日期相差的天数，关心时 分 秒
     * @param before
     * @param after
     * @return
     */
    public static int getIntervalDays(Date before, Date after) {
        if (null == before || null == after) {
            return -1;
        }
        long intervalMilli = after.getTime() - before.getTime();
        return (int) (intervalMilli / (24 * 60 * 60 * 1000));
    }
    /**
     * 计算两个日期相差的天数，只关心天
     * @param before
     * @param after
     * @return
     */
    public static int daysBetween(Date before, Date after) {
//        Calendar aCalendar = Calendar.getInstance();
//        aCalendar.setTime(before);
//        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
//        aCalendar.setTime(after);
//        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
//        return day2 - day1;

        long from = before.getTime();
        long to = after.getTime();
        int days = (int) ((to - from)/(1000 * 60 * 60 * 24));
        return days;
    }

    /**
     * 秒转成yyyy-MM-dd HH:mm:ss
     * @param second
     * @return
     */
    public static String secondToSimpleDateTimeString(long second) {
        Date date = getDateByTimeStamp(second);
//        long coefficient = 1000l;
//        Date date = new Date(second * coefficient);
        SimpleDateFormat formatter = new SimpleDateFormat(SIMPLE_DATE_TIME);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * yyyy-MM-dd HH:mm:ss转成long型数据
     * @param dateString
     * @return
     */
    public static long simpleDateTimeStringToSecond(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat(SIMPLE_DATE_TIME);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date.getTime()/1000;
    }

    public static String toCurrentDateTimeString(String dateFormat) {
        SimpleDateFormat f = new SimpleDateFormat(dateFormat);
        String format = f.format(new Date());
        return format;
    }

    public static String toDateTimeString(String dateFormat, Date date) {
        SimpleDateFormat f = new SimpleDateFormat(dateFormat);
        String format = f.format(date);
        return format;
    }

    /**
     * 根据时间戳返回Date，
     * @param timeStamp  毫秒 或 秒
     * @return
     */
    public static Date getDateByTimeStamp(Long timeStamp) {
        if (timeStamp == null) {
            return new Date();
        }
        String s = String.valueOf(timeStamp);
        if (s.length() == 13) {
            return new Date(timeStamp);
        }
        if (s.length() == 10) {
            return new Date(timeStamp * 1000l);
        }
        return new Date();
    }

    public static Date addDateDay(Date date, int count) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        c.add(Calendar.DATE, count); //日期加1天
//     c.add(Calendar.DATE, -1); //日期减1天
        date = c.getTime();
        return date;
    }

    /**
     * 根据 fomart 比较两个时间大小
     * @param format yyyy-MM-dd
     * @param date1
     * @param date2
     * @return -1 date1<date2,1 date1>date2,0 date1=date2
     */
    public static int dateCompare(String format, String date1, String date2) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if(dt1.after(dt2))
            {
                return 1;
            }else if(dt1.equals(dt2))
            {
                return 0;
            }else {
                return -1;
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return 99;
        }
    }

    //region 获取年、月、日、时、分、秒

    public static int getYear(Date date) {
        return Integer.valueOf(toDateTimeString("yyyy", date));
    }
    public static int getMonth(Date date) {
        return Integer.valueOf(toDateTimeString("MM", date));
    }
    public static int getDay(Date date) {
        return Integer.valueOf(toDateTimeString("dd", date));
    }
    public static int getHour(Date date) {
        return Integer.valueOf(toDateTimeString("HH", date));
    }
    public static int getMinute(Date date) {
        return Integer.valueOf(toDateTimeString("mm", date));
    }
    public static int getSecond(Date date) {
        return Integer.valueOf(toDateTimeString("ss", date));
    }
    //endregion
    
    
    public static void main(String[] args) {
    	String time = DateTimeUtils.toCompactDateTimeString(new Date());
    	System.out.println(time);
	}
}
