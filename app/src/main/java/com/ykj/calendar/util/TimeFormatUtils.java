package com.ykj.calendar.util;

import com.ykj.calendar.bean.MonthDataBeanV3;
import com.ykj.calendar.bean.SeasonDateBeanV3;
import com.ykj.calendar.bean.WeekBeanV3;
import com.ykj.calendar.bean.WeekDateBeanV3;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/18
 * Description: 时间处理工具类
 */
public class TimeFormatUtils {

    //初始化按天选择日历数据
    public static Integer[][] getDayData(int initYear) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH);
        calendar.set(initYear, 0, 1);

        int count = (currentYear - initYear) * 12 + currentMonth + 2;
        Integer[][] resultList = new Integer[count][2];

        for (int i = 0; i < count; i++) {
            resultList[i][0] = calendar.get(Calendar.YEAR);
            resultList[i][1] = calendar.get(Calendar.MONTH) + 1;
            calendar.add(Calendar.MONTH, 1);
        }
        return resultList;
    }

    //生成按周计算的数据
    public static List<WeekBeanV3> getWeekData(int initYear) {
//        int initYear = 2015;
        List<WeekBeanV3> weekBeans = new ArrayList<>();
        Calendar currentTime = Calendar.getInstance();
        int currentYear = currentTime.get(Calendar.YEAR);
        if (currentYear < initYear) {
            //系统时间有问题
//            ToastUtil.show(MainApplication.context(), "系统时间有问题");
            return weekBeans;
        }
        //由于华某为部分手机new Date()生成后立刻调用getWeekOfYear获取最大周数会出现最大周数不准确的问题,所以提前初始化Date
        Date date = new Date();
        int yearLength = currentYear - initYear;
        for (int i = 0; i <= yearLength; i++) {
            int year = initYear + i;
            WeekBeanV3 weekBean = new WeekBeanV3();
            weekBean.year = year + "";

            Calendar c = Calendar.getInstance();
            c.set(year, Calendar.JANUARY, 1);
            c.setFirstDayOfWeek(Calendar.SUNDAY);
            c.setMinimalDaysInFirstWeek(1);//设置每周最少为1天

            int maxWeeks;
            if (year == currentYear) {//当前年
                maxWeeks = getWeekOfYear(date);
            } else {
                maxWeeks = c.getActualMaximum(Calendar.WEEK_OF_YEAR);
            }
            if (maxWeeks > 52) {
                maxWeeks = 52;
            }
            List<WeekDateBeanV3> weekDateBeans = new ArrayList<>();
            for (int j = 0; j <= maxWeeks; j++) {
                WeekDateBeanV3 weekDateBean = new WeekDateBeanV3();

                String startTime = "", endTime = "";
                if (j == 0) {
                    startTime = "01-" + "01";
                    endTime = getLastDayOfWeek(year, j);
                } else if (j == maxWeeks) {
                    startTime = getFirstDayOfWeek(year, j);
                    if (year == currentTime.get(Calendar.YEAR)) {
                        endTime = getLastDayOfWeek(year, j);
                    } else {
                        endTime = "12-" + "31";
                    }
                } else {
                    startTime = getFirstDayOfWeek(year, j);
                    endTime = getLastDayOfWeek(year, j);
                }
                weekDateBean.year = weekBean.year;
                weekDateBean.week = String.valueOf(j + 1);
                weekDateBean.endDate = year + "-" + endTime;
                weekDateBean.startDate = year + "-" + startTime;
                weekDateBeans.add(0,weekDateBean);
            }

            weekBean.weekDateBeanList = weekDateBeans;
            weekBeans.add(0,weekBean);
        }
        return weekBeans;
    }

    public static List<MonthDataBeanV3> getMonthData(int initYear) {
        DecimalFormat df = new DecimalFormat("00");
        List<MonthDataBeanV3> monthDataBeans = new ArrayList<>();
        Calendar endCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(initYear, Calendar.JANUARY, 1);

        int month = 0;
        while (startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) {
            ++month;
            if (month > 12) {
                month = 1;
            }
            MonthDataBeanV3 monthDataBean = new MonthDataBeanV3();
            String startTime = "", endTime = "";
            startTime = startCalendar.get(Calendar.YEAR) + "-" + df.format(startCalendar.get(Calendar.MONTH) + 1) + "-" + "01";
            endTime = startCalendar.get(Calendar.YEAR) + "-" + df.format(startCalendar.get(Calendar.MONTH) + 1) + "-" + startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            //年:startCalendar.get(Calendar.YEAR), 月:month, 开始时间:startTime, 结束时间:endTime
            monthDataBean.year = startCalendar.get(Calendar.YEAR) + "";
            monthDataBean.month = month + "";
            monthDataBean.startDate = startTime;
            monthDataBean.endDate = endTime;
            monthDataBeans.add(0, monthDataBean);

            startCalendar.add(Calendar.MONTH, 1);
        }
        return monthDataBeans;
    }

    //生成按季度计算的数据
    public static List<SeasonDateBeanV3> getSeasonData(int initYear) {
        DecimalFormat df = new DecimalFormat("00");
        List<SeasonDateBeanV3> seasonDateBeans = new ArrayList<>();
        Calendar endCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(initYear, Calendar.JANUARY, 1);
//        List<String> dateString = new ArrayList<>();
        int season = 0;
        while (startCalendar.getTimeInMillis() < endCalendar.getTimeInMillis()) {
            ++season;
            if (season > 4) {
                season = 1;
            }
            SeasonDateBeanV3 seasonDateBean = new SeasonDateBeanV3();
            String startTime = "", endTime = "";
            startTime = startCalendar.get(Calendar.YEAR) + "-" + df.format(startCalendar.get(Calendar.MONTH) + 1) + "-" + "01";
            startCalendar.add(Calendar.MONTH, 2);
            endTime = startCalendar.get(Calendar.YEAR) + "-" + df.format(startCalendar.get(Calendar.MONTH) + 1) + "-" + startCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            startCalendar.add(Calendar.MONTH, -2);
//            dateString.add("年：" + startCalendar.get(Calendar.YEAR) + "-季度:" + season + " 开始时间:" + startTime + " 结束时间:" + endTime);
            seasonDateBean.year = startCalendar.get(Calendar.YEAR) + "";
            seasonDateBean.season = season + "";
            seasonDateBean.startDate = startTime;
            seasonDateBean.endDate = endTime;
            seasonDateBeans.add(0, seasonDateBean);
            startCalendar.add(Calendar.MONTH, 3);

        }
        return seasonDateBeans;
    }

    private static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    // 获取某年的第几周的开始日期
    public static String getFirstDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.JANUARY, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);
        return getFirstDayOfWeek(cal.getTime());
    }

    // 获取某年的第几周的结束日期
    public static String getLastDayOfWeek(int year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(year, Calendar.JANUARY, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, week * 7);
        return getLastDayOfWeek(cal.getTime());
    }

    // 获取当前时间所在周的开始日期
    public static String getFirstDayOfWeek(Date date) {
        DecimalFormat df = new DecimalFormat("00");
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return df.format(c.get(Calendar.MONTH) + 1) + "-" + df.format(c.get(Calendar.DAY_OF_MONTH));
    }

    // 获取当前时间所在周的结束日期
    public static String getLastDayOfWeek(Date date) {
        DecimalFormat df = new DecimalFormat("00");
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return df.format(c.get(Calendar.MONTH) + 1) + "-" + df.format(c.get(Calendar.DAY_OF_MONTH));
    }

    // 获取当前年份格式化字符串
    public static String getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return new SimpleDateFormat("yyyy").format(cal.getTime());
    }

    // 获取当前周是本年度第几周
    public static int getWeekOfCurrentYear() {
        Calendar c=Calendar.getInstance();
        return c.get(Calendar.WEEK_OF_YEAR);
    }

    // 获取当月第一天格式化字符串
    public static String getFirstDayOfMonth() {
        Calendar cDay = Calendar.getInstance();
        cDay.setTime(new Date());
        cDay.set(Calendar.DAY_OF_MONTH, 1);
        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        return createTimeSdf1.format(cDay.getTime());
    }

    // 获取当月最后一天格式化字符串
    public static String getEndDayOfMonth() {
        SimpleDateFormat createTimeSdf1 = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return createTimeSdf1.format(ca.getTime());
    }

    // 获取当前年份-月份格式化字符串
    public static String getThisMonth() {
        SimpleDateFormat createTimeSdf = new SimpleDateFormat("yyyy-M");
        return createTimeSdf.format(new Date());
    }

    // 获取昨天的格式化字符串
    public static String getLastDay() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
    }

    // 获取本周第一天的格式化字符串
    public static String getWeekFirstDay() {
        DecimalFormat df = new DecimalFormat("00");
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.get(Calendar.YEAR)+"-"+df.format(c.get(Calendar.MONTH) + 1) + "-" + df.format(c.get(Calendar.DAY_OF_MONTH));
    }

    // 获取本周最后一天的格式化字符串
    public static String getWeekEndDay() {
        DecimalFormat df = new DecimalFormat("00");
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.get(Calendar.YEAR) + "-" + df.format(c.get(Calendar.MONTH) + 1) + "-" + df.format(c.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar timeStrToCalendar(String timeStr) {
        if (timeStr == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(timeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long formatTimeToMill(String timeStr) {
        if (timeStr == null) {
            return 0;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long mills = 0;
        try {
            mills = sdf.parse(timeStr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mills;
    }
}
