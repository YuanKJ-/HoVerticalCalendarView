package com.wehotel.calendar.bean;

public class WeekDateBeanV3 {

    private String week; //本年第几周
    private String startDate; //本周起始日期
    private String endDate; //本周结束日期

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

}