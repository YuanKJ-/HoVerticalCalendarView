package com.wehotel.calendar.util;

import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;

import java.text.DecimalFormat;
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

    public static List<WeekBeanV3> getWeekBeans() {
        int initYear = 2015;
        List<WeekBeanV3> weekBeans = new ArrayList<>();
        Calendar currentTime = Calendar.getInstance();
        int currentYear = currentTime.get(Calendar.YEAR);
        if (currentYear < 2015) {
            //系统时间有问题
//            ToastUtil.show(MainApplication.context(), "系统时间有问题");
            return null;
        }
        int yearLength = currentYear - initYear;
        for (int i = 0; i < yearLength; i++) {
            int year = initYear + 1 + i;
            WeekBeanV3 weekBean = new WeekBeanV3();
            weekBean.setYear(year + "");

            Calendar c = Calendar.getInstance();
            c.set(year, Calendar.JANUARY, 1);
            c.setFirstDayOfWeek(Calendar.SUNDAY);
            c.setMinimalDaysInFirstWeek(1);//设置每周最少为1天

            int maxWeeks;
            if (year == currentYear) {//当前年
                maxWeeks = getWeekOfYear(new Date());
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
                weekDateBean.setWeek(String.valueOf(j + 1));
                weekDateBean.setEndDate(year + "-" + endTime);
                weekDateBean.setStartDate(year + "-" + startTime);
                weekDateBeans.add(0,weekDateBean);
            }

            weekBean.setWeekDateBeanList(weekDateBeans);
            weekBeans.add(0,weekBean);
        }
        return weekBeans;
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
}
