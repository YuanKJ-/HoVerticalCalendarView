package com.wehotel.calendar.bean;

import com.wehotel.calendar.util.TimeFormatUtils;

import java.util.Calendar;

public class WeekDateBeanV3 {
    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    public String week; //本年第几周
    public String startDate; //本周起始日期
    public String endDate; //本周结束日期

    //生成并缓存展示在列表中的数据形式
    public String showDate;
    public String showWeek;

    public int type; //item类型
    public String year; //头部显示年份
    public int bindTitlePosition; //绑定大类选中位置

    public WeekDateBeanV3() {
        this.type = ITEM_TYPE;
    }

    public WeekDateBeanV3(String year) {
        this.type = HEADER_TYPE;
        this.year = year;
    }

    /**
     * 生成并缓存展示在列表中的数据
     */
    public void initShowData() {
        if (showWeek != null) {
            return;
        }
        showWeek = "第" + week + "周";
        Calendar startCalendar = TimeFormatUtils.timeStrToCalendar(startDate);
        Calendar endCalendar = TimeFormatUtils.timeStrToCalendar(endDate);
        if (startCalendar != null && endCalendar != null) {
            showDate = "(" +
                    (startCalendar.get(Calendar.MONTH) + 1) + "月" + startCalendar.get(Calendar.DAY_OF_MONTH) + "日"
                    + "-" +
                    (endCalendar.get(Calendar.MONTH) + 1) + "月" + endCalendar.get(Calendar.DAY_OF_MONTH) + "日"
                    + ")";
        }
    }
}