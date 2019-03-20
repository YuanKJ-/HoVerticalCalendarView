package com.wehotel.calendar.bean;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联合滚动列表BaseValueBean基类
 */
public class BaseValueBean {

    public static final int HEADER_TYPE = 0;
    public static final int ITEM_TYPE = 1;

    public int type; //item类型
    public String year; //头部显示年份
    public int bindTitlePosition; //绑定大类选中位置
}
