package com.ykj.calendar.bean;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 按月数据ValueBean
 */
public class MonthDataBeanV3 extends BaseValueBean {

    public String month;
    public String startDate;
    public String endDate;

    public MonthDataBeanV3() {
        this.type = ITEM_TYPE;
    }

    public MonthDataBeanV3(String year) {
        this.type = HEADER_TYPE;
        this.year = year;
    }
}
