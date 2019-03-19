package com.wehotel.calendar.bean;

public class WeekDateBeanV3 {
    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    public String week; //本年第几周
    public String startDate; //本周起始日期
    public String endDate; //本周结束日期

    public int type; //item类型
    public String year; //头部显示年份
    public int bindTitlePosition; //绑定大类选中位置

    public WeekDateBeanV3() {
        this.type = ITEM_TYPE;
    }

    public WeekDateBeanV3(String year) {
        this.type = HEADER_TYPE;
    }
}