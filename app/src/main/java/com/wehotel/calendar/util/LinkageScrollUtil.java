package com.wehotel.calendar.util;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.wehotel.calendar.adapter.BaseTitleAdapter;
import com.wehotel.calendar.adapter.BaseValueAdapter;
import com.wehotel.calendar.bean.BaseTitleBean;
import com.wehotel.calendar.bean.BaseValueBean;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;

/**
 * Created by kejie.yuan
 * Date: 2019/3/20
 * Description: 联动列表工具类
 */
public class LinkageScrollUtil {

    public static void bindLinkageScroll(RecyclerView titleRv, final RecyclerView valueRv) {
        //初始化联合滚动效果
        final BaseTitleAdapter titleAdapter = (BaseTitleAdapter) titleRv.getAdapter();
        titleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseTitleBean item = (BaseTitleBean) titleAdapter.getItem(position);
                if (item != null) {
                    ((LinearLayoutManager) valueRv.getLayoutManager()).scrollToPositionWithOffset(item.bindValuePosition, 0);
                    titleAdapter.setSelectedYear(item.year);
                    titleAdapter.notifyDataSetChanged();
                }
            }
        });
        final BaseValueAdapter valueAdapter = (BaseValueAdapter) valueRv.getAdapter();
        valueRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //获取右侧列表的第一个可见Item的position
                int topPosition = ((LinearLayoutManager) valueRv.getLayoutManager()).findFirstVisibleItemPosition();
                // 如果此项对应的是左边的大类的index
                BaseValueBean item = (BaseValueBean) valueAdapter.getItem(topPosition);
                if (item != null) {
                    int bindPos = item.bindTitlePosition;
                    titleAdapter.updateSelectedPos(bindPos);
                }
            }
        });
    }
}
