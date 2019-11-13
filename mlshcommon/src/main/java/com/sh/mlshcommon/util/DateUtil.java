package com.sh.mlshcommon.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    public static final String COMPACT_DATE_TIME2 = "yyyyMMddHHmmssSSS";
    public static final String YYYYMMDDHHSSMM = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYYMMDD = "yyyy-MM-dd";
    public static final String YYYYMMDD2 = "yyyyMMdd";

    private DateUtil(){

    }

    private static String getDateFormat(Date date, String dateFormatType) {
        SimpleDateFormat simformat = new SimpleDateFormat(dateFormatType);
        return simformat.format(date);
    }

    public static String formatCSTTime(String date, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        Date d = sdf.parse(date);
        return DateUtil.getDateFormat(d, format);
    }

    public static Date addDateMinute(Date date, int count) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        c.add(Calendar.MINUTE, count); //分钟加
        date = c.getTime();
        return date;
    }

    public static Date addDate(Date date, int count) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        c.add(Calendar.DATE, count); //天加
        date = c.getTime();
        return date;
    }

    public static Date addDateStart(Date date, int count) {
        if (null == date) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);   //设置当前日期
        c.add(Calendar.DATE, count); //天加
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        date = c.getTime();
        return date;
    }

    public static Date firstDayOfMonth(String month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date parse = dateFormat.parse(month, new ParsePosition(0));
        Calendar instance = Calendar.getInstance();
        instance.setTime(parse);
        instance.set(Calendar.DAY_OF_MONTH,1);
        return instance.getTime();
    }

    public static Date endDayOfMonth(String month) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        Date parse = dateFormat.parse(month, new ParsePosition(0));
        Calendar instance = Calendar.getInstance();
        instance.setTime(parse);
        instance.set(Calendar.DAY_OF_MONTH,instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        return instance.getTime();
    }

    public static String formatDate(Date day, String s) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(s);
        return dateFormat.format(day);
    }

    public static Date parseDate(String s, String s1) {
        try{
            SimpleDateFormat dateFormat = new SimpleDateFormat(s1);
            return dateFormat.parse(s,new ParsePosition(0));
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String dateToLong(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat(COMPACT_DATE_TIME2);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static LocalDateTime parseLocalDateTime(String time,String format) {
        if(StringUtil.isEmpty(time)){
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    public static LocalDate parseLocalDate(String time,String format) {
        if(StringUtil.isEmpty(time)){
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(time,dateTimeFormatter);
    }

    public static String formatLocalDate(LocalDate localDate,String format) {
        if(StringUtil.isNull(localDate)){
            return null;
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return dateTimeFormatter.format(localDate);
    }

    public static LocalDate minuLocalDate(LocalDate now, int i) {
        return now.minusDays(i);
    }
}
