package com.wehotel.calendar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chad.library.adapter.base.BaseViewHolder;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.adapter.BaseTitleAdapter;
import com.wehotel.calendar.adapter.BaseValueAdapter;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;
import com.wehotel.calendar.util.LinkageScrollUtil;
import com.wehotel.calendar.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/18
 * Description: 按周选择View
 */
public class WeekSelectView extends ConstraintLayout {
    private static final String TAG = "WeekSelectView";

    private static final int INIT_YEAR = 2015;

    private RecyclerView titleList;
    private RecyclerView valueList;

    private BaseTitleAdapter<WeekBeanV3, BaseViewHolder> titleAdapter;
    private ValueAdapter valueAdapter;

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

        List<WeekBeanV3> titleBeans = new ArrayList<>();
        List<WeekDateBeanV3> valueBeans = new ArrayList<>();
        generateData(titleBeans, valueBeans);

        titleList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        valueList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        titleAdapter = new BaseTitleAdapter<>(titleBeans);
        titleList.setAdapter(titleAdapter);

        valueAdapter = new ValueAdapter(valueBeans);
        valueList.setAdapter(valueAdapter);

        //绑定联合滚动
        LinkageScrollUtil.bindLinkageScroll(titleList, valueList);
    }

    private void generateData(List<WeekBeanV3> titleBeans, List<WeekDateBeanV3> valueBeans) {
        //生成源数据
        List<WeekBeanV3> sourceData = TimeFormatUtils.getWeekData(INIT_YEAR);
        //生成titleBeans数据
        titleBeans.addAll(sourceData);
        //生成valueBeans数据并绑定pos
        for (int i = 0; i < titleBeans.size(); i++) {
            WeekBeanV3 weekBean = titleBeans.get(i);
            List<WeekDateBeanV3> dateBeans = weekBean.weekDateBeanList;
            //value列表分组头部绑定title分组pos
            WeekDateBeanV3 weekHeader = new WeekDateBeanV3(weekBean.year);
            weekHeader.bindTitlePosition = i;
            valueBeans.add(weekHeader);
            //title分组绑定value分组头部pos
            weekBean.bindValuePosition = valueBeans.size() - 1;
            for (WeekDateBeanV3 dateBeanV3 : dateBeans) {
                dateBeanV3.bindTitlePosition = i;
                dateBeanV3.initShowData();
                valueBeans.add(dateBeanV3);
            }
        }
    }

    private static class ValueAdapter extends BaseValueAdapter<WeekDateBeanV3, BaseViewHolder> {

        ValueAdapter(@Nullable List<WeekDateBeanV3> data) {
            super(data);
        }

        @Override
        protected void convert(BaseViewHolder helper, WeekDateBeanV3 item) {
            super.convert(helper, item);
            if (item.type == WeekDateBeanV3.ITEM_TYPE) {
                helper.setText(R.id.primary_text, item.showWeek);
                helper.setText(R.id.sub_text, item.showDate);
                if (helper.getAdapterPosition() == 1) {
                    helper.setText(R.id.current_tips, "本周");
                    helper.setVisible(R.id.current_tips, true);
                } else {
                    helper.setGone(R.id.current_tips, false);
                }
                // TODO: 2019/3/19 判断是否是当前日期,设置选中
            }
        }
    }
}
