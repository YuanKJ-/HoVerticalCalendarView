package com.ykj.calendar.bean;

/**
 * Created by kejie.yuan
 * Date: 2019/4/21
 * Description: 带有头部的通用HeaderValueBean
 */
public class HeaderValueBean extends BaseBindPositionBean {

    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    public int type; //item类型
    public String year; //头部显示年份
}
