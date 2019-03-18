package com.wehotel.calendar.bean;

import java.util.List;

public class WeekBeanV3 {

    private String year;
    private List<WeekDateBeanV3> weekDateBeanList;
    private int bindValuePosition; //绑定value列表自动跳转位置

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<WeekDateBeanV3> getWeekDateBeanList() {
        return weekDateBeanList;
    }

    public void setWeekDateBeanList(List<WeekDateBeanV3> weekDateBeanList) {
        this.weekDateBeanList = weekDateBeanList;
    }
}