package com.ykj.calendar.bean;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 按季度数据ValueBean
 */
public class SeasonDateBeanV3 extends HeaderValueBean {

    public String season;
    public String startDate;
    public String endDate;

    public SeasonDateBeanV3() {
        this.type = ITEM_TYPE;
    }

    public SeasonDateBeanV3(String year) {
        this.type = HEADER_TYPE;
        this.year = year;
    }
}