package com.ykj.calendar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/14
 * Description: 日历控件PagerAdapter
 */
public class CalendarPagerAdapter extends PagerAdapter {

    private Context context;
    private List<View> list;

    public CalendarPagerAdapter(Context context, List<View> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));// 删除页卡
    }

    // 这个方法用来实例化页卡
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(list.get(position), 0);// 添加页卡
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
