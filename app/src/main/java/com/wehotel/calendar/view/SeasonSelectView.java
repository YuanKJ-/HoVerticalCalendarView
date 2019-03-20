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
import com.wehotel.calendar.adapter.BaseTitleAdapter;
import com.wehotel.calendar.adapter.BaseValueAdapter;
import com.wehotel.calendar.bean.BaseTitleBean;
import com.wehotel.calendar.bean.BaseValueBean;
import com.wehotel.calendar.bean.SeasonDateBeanV3;
import com.wehotel.calendar.bean.WeekBeanV3;
import com.wehotel.calendar.bean.WeekDateBeanV3;
import com.wehotel.calendar.util.LinkageScrollUtil;
import com.wehotel.calendar.util.TimeFormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/18
 * Description: 按季度选择View
 */
public class SeasonSelectView extends ConstraintLayout {
    private static final String TAG = "SeasonSelectView";

    private static final int INIT_YEAR = 2015;

    private RecyclerView titleList;
    private RecyclerView valueList;

    private BaseTitleAdapter<BaseTitleBean, BaseViewHolder> titleAdapter;
    private ValueAdapter valueAdapter;

    public SeasonSelectView(Context context) {
        this(context, null);
    }

    public SeasonSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeasonSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.linkage_rv_layout, this);
        titleList = (RecyclerView) findViewById(R.id.title_rv);
        valueList = (RecyclerView) findViewById(R.id.value_rv);

        List<BaseTitleBean> titleBeans = new ArrayList<>();
        List<SeasonDateBeanV3> valueBeans = new ArrayList<>();
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

    private void generateData(List<BaseTitleBean> titleBeans, List<SeasonDateBeanV3> valueBeans) {
        //生成season原始数据
        List<SeasonDateBeanV3> sourceDatas = TimeFormatUtils.getSeasonDates(INIT_YEAR);
        //生成season title 和 value 数据
        String currentGenerateYear = "";
        for (int i = 0; i < sourceDatas.size(); i++) {
            SeasonDateBeanV3 seasonDate = sourceDatas.get(i);
            if (!seasonDate.year.equals(currentGenerateYear)) {
                currentGenerateYear = seasonDate.year;
                //生成titleBean
                BaseTitleBean baseTitleBean = new BaseTitleBean();
                baseTitleBean.year = currentGenerateYear;
                baseTitleBean.bindValuePosition = valueBeans.size();
                titleBeans.add(baseTitleBean);
                //生成valueBean头部
                SeasonDateBeanV3 seasonHeader = new SeasonDateBeanV3(currentGenerateYear);
                seasonHeader.bindTitlePosition = titleBeans.size() - 1;
                valueBeans.add(seasonHeader);
            }
            seasonDate.bindTitlePosition = titleBeans.size() - 1;
            valueBeans.add(seasonDate);
        }
    }

    private static class ValueAdapter extends BaseValueAdapter<SeasonDateBeanV3, BaseViewHolder> {

        ValueAdapter(@Nullable List<SeasonDateBeanV3> data) {
            super(data);
        }

        @Override
        protected void convert(BaseViewHolder helper, SeasonDateBeanV3 item) {
            super.convert(helper, item);
            if (item.type == BaseValueBean.ITEM_TYPE) {
                helper.setText(R.id.primary_text, "第" + item.season + "季度");
                helper.setGone(R.id.sub_text, false);
                if (helper.getAdapterPosition() == 1) {
                    helper.setText(R.id.current_tips, "本季度");
                    helper.setVisible(R.id.current_tips, true);
                } else {
                    helper.setGone(R.id.current_tips, false);
                }
                // TODO: 2019/3/19 判断是否是当前日期,设置选中
            }
        }
    }
}
