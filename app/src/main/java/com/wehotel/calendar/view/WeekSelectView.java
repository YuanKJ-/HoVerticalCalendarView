package com.wehotel.calendar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;
import com.wehotel.calendar.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/18
 * Description: 按周选择View
 */
public class WeekSelectView extends ConstraintLayout {
    private static final String TAG = "WeekSelectView";

    private RecyclerView titleList;
    private RecyclerView valueList;

    public WeekSelectView(Context context) {
        this(context, null);
    }

    public WeekSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeekSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.linkage_rv_layout, this);
        titleList = (RecyclerView) findViewById(R.id.title_rv);
        valueList = (RecyclerView) findViewById(R.id.value_rv);
        int initYear = 2015;
        List<WeekBeanV3> weekBeans = TimeFormatUtils.getWeekBeans();
        Log.d(TAG, "WeekSelectView: ");
        List<WeekDateBeanV3> weekDataBeans = new ArrayList<>();
        for (int i = 0; i < weekBeans.size(); i++) {
            WeekBeanV3 weekBean = weekBeans.get(i);
            List<WeekDateBeanV3> dateBeans = weekBean.weekDateBeanList;
            //value列表分组头部绑定title分组pos
            WeekDateBeanV3 weekHeader = new WeekDateBeanV3(weekBean.year);
            weekHeader.bindTitlePosition = i;
            weekDataBeans.add(weekHeader);
            //title分组绑定value分组头部pos
            weekBean.bindValuePosition = weekDataBeans.size() - 1;
            for (WeekDateBeanV3 dateBeanV3 : dateBeans) {
                dateBeanV3.bindTitlePosition = i;
                weekDataBeans.add(dateBeanV3);
            }
        }
        titleList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        valueList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        TitleAdapter titleAdapter = new TitleAdapter(weekBeans);
        titleList.setAdapter(titleAdapter);

        ValueAdapter valueAdapter = new ValueAdapter(weekDataBeans);
        valueList.setAdapter(valueAdapter);
    }

    private static class TitleAdapter extends BaseQuickAdapter<WeekBeanV3, BaseViewHolder> {
        private String selectedYear;

        public TitleAdapter(@Nullable List<WeekBeanV3> data) {
            super(R.layout.year_item, data);
            if (data != null && data.size() > 0) {
                selectedYear = data.get(0).year;
            }
        }

        @Override
        protected void convert(BaseViewHolder helper, WeekBeanV3 item) {
            String text = item.year + "年";
            helper.setText(R.id.year_text, text);
            if (item.year.equals(selectedYear)) {
                //设置选中
                helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.white));
            } else {
                helper.setBackgroundColor(R.id.year_text, mContext.getResources().getColor(android.R.color.transparent));
            }
        }
    }

    private static class ValueAdapter extends BaseQuickAdapter<WeekDateBeanV3, BaseViewHolder> {

        public ValueAdapter(@Nullable List<WeekDateBeanV3> data) {
            super(data);
            //Step.1
            setMultiTypeDelegate(new MultiTypeDelegate<WeekDateBeanV3>() {
                @Override
                protected int getItemType(WeekDateBeanV3 entity) {
                    //根据你的实体类来判断布局类型
                    return entity.type;
                }
            });
            //Step.2
            getMultiTypeDelegate()
                    .registerItemType(WeekDateBeanV3.HEADER_TYPE, R.layout.value_header_view)
                    .registerItemType(WeekDateBeanV3.ITEM_TYPE, R.layout.value_item_view);
        }

        @Override
        protected void convert(BaseViewHolder helper, WeekDateBeanV3 item) {

        }
    }
}
