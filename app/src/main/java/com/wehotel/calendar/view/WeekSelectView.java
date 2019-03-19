package com.wehotel.calendar.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;
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

    private RecyclerView titleList;
    private RecyclerView valueList;

    private TitleAdapter titleAdapter;
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

        titleAdapter = new TitleAdapter(weekBeans);
        titleList.setAdapter(titleAdapter);

        valueAdapter = new ValueAdapter(weekDataBeans);
        valueList.setAdapter(valueAdapter);

        initLinkageScroll();
    }

    //初始化联合滚动效果
    private void initLinkageScroll() {
        titleAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                WeekBeanV3 weekBeanV3 = titleAdapter.getItem(position);
                if(weekBeanV3 != null) {
                    titleAdapter.setSelectedYear(weekBeanV3.year);
                    titleAdapter.notifyDataSetChanged();
                    ((LinearLayoutManager) valueList.getLayoutManager()).scrollToPositionWithOffset(weekBeanV3.bindValuePosition, 0);
                }
            }
        });
        valueList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //获取右侧列表的第一个可见Item的position
                int topPosition = ((LinearLayoutManager) valueList.getLayoutManager()).findFirstVisibleItemPosition();
                // 如果此项对应的是左边的大类的index
                WeekDateBeanV3 weekDateBeanV3 = valueAdapter.getItem(topPosition);
                if (weekDateBeanV3 != null) {
                    int bindPos = weekDateBeanV3.bindTitlePosition;
                    titleAdapter.updateSelectedPos(bindPos);
                }
            }
        });
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

        public void setSelectedYear(String selectedYear) {
            this.selectedYear = selectedYear;
        }

        public void updateSelectedPos(int pos) {
            WeekBeanV3 weekBeanV3 = getItem(pos);
            if (weekBeanV3 != null && !weekBeanV3.year.equals(selectedYear)) {
                selectedYear = weekBeanV3.year;
                notifyDataSetChanged();
            }
        }
    }

    private static class ValueAdapter extends BaseQuickAdapter<WeekDateBeanV3, BaseViewHolder> {

        public ValueAdapter(@Nullable List<WeekDateBeanV3> data) {
            super(data);
            setMultiTypeDelegate(new MultiTypeDelegate<WeekDateBeanV3>() {
                @Override
                protected int getItemType(WeekDateBeanV3 entity) {
                    //根据你的实体类来判断布局类型
                    return entity.type;
                }
            });
            getMultiTypeDelegate()
                    .registerItemType(WeekDateBeanV3.HEADER_TYPE, R.layout.value_header_view)
                    .registerItemType(WeekDateBeanV3.ITEM_TYPE, R.layout.value_item_view);
        }

        @Override
        protected void convert(BaseViewHolder helper, WeekDateBeanV3 item) {
            if (item.type == WeekDateBeanV3.HEADER_TYPE) {
                helper.setText(R.id.header_text, item.year + "年");
            } else if (item.type == WeekDateBeanV3.ITEM_TYPE) {
                item.initShowData();
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
