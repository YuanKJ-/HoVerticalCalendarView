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
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.bean.WeekBeanV3;
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
        List<YearModel> yearModels = new ArrayList<>();
        Calendar currentTime = Calendar.getInstance();
        int currentYear = currentTime.get(Calendar.YEAR);
        for(int i = currentYear; i>initYear; i--) {
            YearModel yearModel = new YearModel();
            yearModel.year = i;
            yearModel.isSelected = false;
            yearModels.add(yearModel);
        }
        TitleAdapter titleAdapter = new TitleAdapter(yearModels);
        titleList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        titleList.setAdapter(titleAdapter);
    }

    private static class YearModel {
        public int year;
        public boolean isSelected;
        public int bindValPosition;
    }

    private static class WeekModel {
        public String weekCount; //第几周
        public String startDate; //起始日期
        public String endDate; //结束日期
    }

    private static class TitleAdapter extends BaseQuickAdapter<YearModel, BaseViewHolder> {

        public TitleAdapter(@Nullable List<YearModel> data) {
            super(R.layout.year_item, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, YearModel item) {

        }
    }
}
