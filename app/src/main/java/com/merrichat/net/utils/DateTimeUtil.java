package com.merrichat.net.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateTimeUtil {

    public static final int SECOND = 1000;//毫秒数  
    public static final int MINUTE = 60 * SECOND;
    public static final int HOUR = 60 * MINUTE;
    public static final int DAY = 24 * HOUR;
    /**
     *  时间戳格式转换
     */
    public static String dayNames[] = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
    public static Date str2Date(String input) throws ParseException {
        // String input = "2004-05-06 03:01:02";
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = format.parse(input);
        return d;

    }

    /* 时间戳转换成字符串 */
    public static String getDateToString(long time, String format) {
        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat(format, Locale.getDefault());
        return sf.format(d);
    }

    public static String format0(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(date);
    }

    public static String formatN(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        return df.format(date);
    }

    public static String format8(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
        return df.format(date);
    }

    public static String formatDaojiShi(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }


    public static String format1(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    public static String format2(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//hh表示的是12小时制，HH才是24小时制 
        return df.format(date);
    }


    public static String format9(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("mm:ss");//hh表示的是12小时制，HH才是24小时制
        return df.format(date);
    }

    public static String format3(Date date) {
        if (date == null) return "";
        String todySDF = "今天 HH:mm";
        String yesterDaySDF = "昨天 HH:mm";
        String otherSDF = "M-d HH:mm";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(todySDF);
            time = sfd.format(date);
            return time;
        } else {
            targetCalendar.add(Calendar.DATE, -1);
            if (dateCalendar.after(targetCalendar)) {
                sfd = new SimpleDateFormat(yesterDaySDF);
                time = sfd.format(date);
                return time;
            }
        }
        sfd = new SimpleDateFormat(otherSDF);
        time = sfd.format(date);
        return time;
    }

    public static String formatNew(Date date) {
        if (date == null) return "";
        String todySDF = "今天 HH:mm";
        String otherSDF = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(todySDF);
            time = sfd.format(date);
            return time;
        }
        sfd = new SimpleDateFormat(otherSDF);
        time = sfd.format(date);
        return time;
    }

    public static String format4(Date date) {
        if (date == null) return "";
        String time = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            df = new SimpleDateFormat("今天");
        } else {
            targetCalendar.add(Calendar.DATE, -1);
            if (dateCalendar.after(targetCalendar)) {
                df = new SimpleDateFormat("昨天");
            }

        }
        return df.format(date);
    }

    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getCurrentTimeYYYYMM() {
        return getCurrentTime("yyyy年MM月");
    }

    /**
     * /获取当前月第一天：
     *
     * @return
     */
    public static String getCurrentMonthFirstTaday() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        return format.format(c.getTime());
    }

    /**
     * /获取当前月最有一天天：
     *
     * @return
     */
    public static String getCurrentMonthLastTaday() {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH,
                ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(ca.getTime());
    }


    /**
     * 某一个月第一天和最后一天
     *
     * @param date
     * @return
     */
    public static Map<String, String> getFirstday_Lastday_Month(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        Date theDate = calendar.getTime();

        // 上个月第一天
        GregorianCalendar gcLast = (GregorianCalendar) Calendar.getInstance();
        gcLast.setTime(theDate);
        gcLast.set(Calendar.DAY_OF_MONTH, 1);
        String day_first = df.format(gcLast.getTime());
        // StringBuffer str = new StringBuffer().append(day_first).append(
        // " 00:00:00");
        // day_first = str.toString();

        // 上个月最后一天
        calendar.add(Calendar.MONTH, 1); // 加一个月
        calendar.set(Calendar.DATE, 1); // 设置为该月第一天
        calendar.add(Calendar.DATE, -1); // 再减一天即为上个月最后一天
        String day_last = df.format(calendar.getTime());
        // StringBuffer endStr = new StringBuffer().append(day_last).append(
        // " 23:59:59");
        // day_last = endStr.toString();

        Map<String, String> map = new HashMap<String, String>();
        map.put("first", day_first);
        map.put("last", day_last);
        return map;
    }

    /**
     * 获得当前月
     * 返回int类型
     *
     * @return
     */
    public static int getCurrentMonth() {
        int i = 12;
        SimpleDateFormat formatter = new SimpleDateFormat("MM月");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        i = Integer.parseInt(str.replace("月", ""));
        return i;
    }

    /**
     * 获取 12-16 11:18
     *
     * @param input
     * @return
     * @throws ParseException
     */
    public static String strDateTime(String input) throws ParseException {
        // String input = "2004-05-06 03:01:02";
        return getCurrentTime("MM-dd mm:ss");

    }

    public static String format5(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
        return df.format(date);
    }

    /**
     * 根据当前时间和评论时的时间计算出该评论的显示时间 （如10分钟前、3小时前、1天前）
     *
     * @param @param createTime  评论创建的时间
     * @param @param systemTime  系统当前时间
     * @return String    返回类型      返回计算后的结果（1分钟前，2天前...）
     * @Title: countCommentTime
     * @Description: 计算评论显示的时间（如1 分钟前或2天前）
     */
    public static String countCommentTime(long createTime, long systemTime) {
        //1分钟毫秒数
        long minuteMillsecond = 1 * 60 * 1000;
        //1小时毫秒数
        long hourMillisecond = 1 * 60 * 60 * 1000;
        //1天毫秒数
        long dayMillisecond = 1 * 24 * 60 * 60 * 1000;

        //计算时间
        long resultTime = systemTime - createTime;
        if (resultTime <= 60000) {
            return "刚刚";
        } else if (resultTime < hourMillisecond) {//大于0，小于1小时
            //对1分钟的毫秒数取余
            long remainder = resultTime % minuteMillsecond;

            return (resultTime - remainder) / minuteMillsecond + "分钟前";

        } else if (resultTime > hourMillisecond && resultTime < dayMillisecond) {//大于1小时，小于1天
            long remainder = resultTime % hourMillisecond;

            return (resultTime - remainder) / hourMillisecond + "小时前";
        } else {//大于1天
            long remainder = resultTime % dayMillisecond;

            return (resultTime - remainder) / dayMillisecond + "天前";

        }

    }


    public static String chatMessageTime(long createTime, long systemTime) {
        long zero = systemTime / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        if (createTime >= zero) {
            return format2(new Date(createTime));
        } else {
            return format0(new Date(createTime));
        }
    }

    public static String NoticeTime(long createTime, long systemTime) {
        long zero = systemTime / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().getRawOffset();
        if (createTime >= zero) {
            return format2(new Date(createTime));
        } else {
            return formatN(new Date(createTime));
        }
    }

    /**
     * 用于消息界面显示
     * 本日的时间显示显示格式为小时
     * :分钟, 早于本日的显示x月x日  小时:分钟
     *
     * @param date
     * @return
     */
    public static String formatDateToMessage(Date date) {
        if (date == null) return "";
        String todySDF = "HH:mm";
        String otherSDF = "MM-dd HH:mm";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(now);
        targetCalendar.set(Calendar.HOUR_OF_DAY, 0);
        targetCalendar.set(Calendar.MINUTE, 0);
        if (dateCalendar.after(targetCalendar)) {
            sfd = new SimpleDateFormat(todySDF);
            time = sfd.format(date);
            return time;
        }
        sfd = new SimpleDateFormat(otherSDF);
        time = sfd.format(date);
        return time;
    }

    public static String format6(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(date);
    }

    public static String format7(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("MM/dd HH:mm");
        return df.format(date);
    }

    public static String formatZan(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("HH:mm yyyy/MM/dd");
        return df.format(date);
    }

    public static String formattime(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("MM/dd HH:mm");//hh表示的是12小时制，HH才是24小时制
        return df.format(date);
    }

    public static String Newformattime(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");//hh表示的是12小时制，HH才是24小时制
        return df.format(date);
    }

    public static String formatForMouthDay(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
        String s = df.format(date).toString();
        if ("0".equals(s.substring(0, 1))) {
            return s.substring(1);
        }
        return s;
    }

    public static String formatAnnounce(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return df.format(date);
    }

    public static String formatMark(Date date) {
        if (date == null) return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return df.format(date);
    }


    /**
     * 毫秒换算成 天/小时/分钟/秒
     *
     * @param ms
     * @return
     */
    public static String formatTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
//        long minute = (ms - day * dd - hour * hh) / mi;
//        long second = (ms - day * dd - hour * hh - minute * mi) / ss;

//        String strDay = day < 10 ? "0" + day : "" + day; //天
//        String strHour = hour < 10 ? "0" + hour : "" + hour;//小时
//        String strMinute = minute < 10 ? "0" + minute : "" + minute;//分钟
//        String strSecond = second < 10 ? "0" + second : "" + second;//秒
        if (day > 0) {
            return day + "天" + hour + "小时";
        } else {
            return hour + "小时";
        }
    }

    public static String getCountTimeByLong(Long l) {
        int hour = 0;
        int minute = 0;
        int second = 0;

        second = l.intValue() / 1000;

        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }
        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        return (getTwoLength(hour) + "小时" + getTwoLength(minute) + "分钟" + getTwoLength(second)+"秒");
    }

    public static String getTwoLength(final int data) {
        if (data < 10) {
            return "0" + data;
        } else {
            return "" + data;
        }
    }

    public static String getNewChatTime(long timesamp) {
        String result = "";
        Calendar todayCalendar = Calendar.getInstance();
        Calendar otherCalendar = Calendar.getInstance();
        otherCalendar.setTimeInMillis(timesamp);

        String timeFormat="M月d日 HH:mm";
        String yearTimeFormat="yyyy年M月d日 HH:mm";
        String am_pm="";
        int hour=otherCalendar.get(Calendar.HOUR_OF_DAY);
        if(hour>=0&&hour<6){
            am_pm="凌晨";
        }else if(hour>=6&&hour<12){
            am_pm="早上";
        }else if(hour==12){
            am_pm="中午";
        }else if(hour>12&&hour<18){
            am_pm="下午";
        }else if(hour>=18){
            am_pm="晚上";
        }
        timeFormat="M月d日 "+ am_pm +"HH:mm";
        yearTimeFormat="yyyy年M月d日 "+ am_pm +"HH:mm";

        boolean yearTemp = todayCalendar.get(Calendar.YEAR)==otherCalendar.get(Calendar.YEAR);
        if(yearTemp){
            int todayMonth=todayCalendar.get(Calendar.MONTH);
            int otherMonth=otherCalendar.get(Calendar.MONTH);
            if(todayMonth==otherMonth){//表示是同一个月
                int temp=todayCalendar.get(Calendar.DATE)-otherCalendar.get(Calendar.DATE);
                switch (temp) {
                    case 0:
                        result = getHourAndMin(timesamp);
                        break;
                    case 1:
                        result = "昨天 " + getHourAndMin(timesamp);
                        break;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        int dayOfMonth = otherCalendar.get(Calendar.WEEK_OF_MONTH);
                        int todayOfMonth=todayCalendar.get(Calendar.WEEK_OF_MONTH);
                        if(dayOfMonth==todayOfMonth){//表示是同一周
                            int dayOfWeek=otherCalendar.get(Calendar.DAY_OF_WEEK);
                            if(dayOfWeek!=1){//判断当前是不是星期日     如想显示为：周日 12:09 可去掉此判断
                                result = dayNames[otherCalendar.get(Calendar.DAY_OF_WEEK)-1] + getHourAndMin(timesamp);
                            }else{
                                result = getTime(timesamp,timeFormat);
                            }
                        }else{
                            result = getTime(timesamp,timeFormat);
                        }
                        break;
                    default:
                        result = getTime(timesamp,timeFormat);
                        break;
                }
            }else{
                result = getTime(timesamp,timeFormat);
            }
        }else{
            result=getYearTime(timesamp,yearTimeFormat);
        }
        return result;
    }

    /**
     * 当天的显示时间格式
     * @param time
     * @return
     */
    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    /**
     * 不同一周的显示时间格式
     * @param time
     * @param timeFormat
     * @return
     */
    public static String getTime(long time,String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        return format.format(new Date(time));
    }

    /**
     * 不同年的显示时间格式
     * @param time
     * @param yearTimeFormat
     * @return
     */
    public static String getYearTime(long time,String yearTimeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(yearTimeFormat);
        return format.format(new Date(time));
    }

}
