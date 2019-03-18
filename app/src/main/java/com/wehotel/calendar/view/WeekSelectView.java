package com.wehotel.calendar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.util.TimeFormatUtils;

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
        titleList = (RecyclerView) findViewById(R.id.title_rv);
        valueList = (RecyclerView) findViewById(R.id.value_rv);
        List<WeekBeanV3> weekBeans = TimeFormatUtils.getWeekBeans();
        Log.d(TAG, "WeekSelectView: ");
    }

    private static class YearBean {
        public String year;
        public boolean isSelected;
        public int bindValPosition;
    }

    private static class WeekBean {
        public String weekCount; //第几周
        public String startDate; //起始日期
        public String endDate; //结束日期
    }

    private static class TitleAdapter extends BaseQuickAdapter<YearBean, BaseViewHolder> {

        public TitleAdapter(@Nullable List<YearBean> data) {
            super(data);
        }

        @Override
        protected void convert(BaseViewHolder helper, YearBean item) {

        }
    }
}
