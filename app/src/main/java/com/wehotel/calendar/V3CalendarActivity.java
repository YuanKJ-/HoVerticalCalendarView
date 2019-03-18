package com.wehotel.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hosle.calendar.verticalcalendar.view.MonthView;
import com.hosle.calendar.verticalcalendar.view.VerticalCalendarView;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.adapter.CalendarPagerAdapter;
import com.wehotel.calendar.view.WeekSelectView;

import net.lucode.hackware.magicindicator.*;
import net.lucode.hackware.magicindicator.buildins.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.*;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.*;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by kejie.yuan
 * Date: 2019/3/14
 * Description: 日历选择通用Activity
 */
public class V3CalendarActivity extends AppCompatActivity {

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_v3);
        initViewPager();
        initMagicIndicator();
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<View> views = new ArrayList<>();
        views.add(initDayView());
        views.add(initWeekView());
        views.add(initMonthView());
        views.add(initQuarterView());
        CalendarPagerAdapter pagerAdapter = new CalendarPagerAdapter(this, views);
        viewPager.setAdapter(pagerAdapter);
    }

    // 初始化indicator
    private void initMagicIndicator() {
        final List<String> mDataList = new ArrayList<String>() {{
            add("按天");
            add("按周");
            add("按月");
            add("按季度");
        }};
        MagicIndicator magicIndicator = (MagicIndicator) findViewById(R.id.magic_indicator);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true); //水平平分空间
        int padding = UIUtil.dip2px(this, 20);
        commonNavigator.setRightPadding(padding);
        commonNavigator.setLeftPadding(padding);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mDataList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
                simplePagerTitleView.setText(mDataList.get(index));
                simplePagerTitleView.setTextSize(16);
                simplePagerTitleView.setNormalColor(Color.parseColor("#999999"));
                simplePagerTitleView.setSelectedColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineWidth(UIUtil.dip2px(V3CalendarActivity.this, 24)); //width 24dp
                indicator.setLineHeight(UIUtil.dip2px(V3CalendarActivity.this, 2)); //height 2dp
                indicator.setColors(Color.parseColor("#4F64FD"));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    //<editor-fold desc="ViewPager 4块选择view">
    /**
     * 初始化按天选择View
     * @return view
     */
    private View initDayView() {
        VerticalCalendarView calendarView = new VerticalCalendarView(this);
        calendarView.setCalendarParams(createMonth(28), new MonthView.OnDayClickListener() {
            @Override
            public void onDayClick(@NotNull MonthView view, @NotNull Calendar day) {
                String dateString = day.get(Calendar.YEAR) +
                        "-" + (day.get(Calendar.MONTH) + 1) + "-" + day.get(Calendar.DAY_OF_MONTH);
                Toast.makeText(V3CalendarActivity.this, dateString, Toast.LENGTH_SHORT).show();
            }
        }, null);
        return calendarView;
    }

    /**
     * 初始化按周选择view
     * @return view
     */
    private View initWeekView() {
        return new WeekSelectView(this);
    }

    /**
     * 初始化按月选择view
     * @return view
     */
    private View initMonthView() {
        return new TextView(this);
    }

    /**
     * 初始化按季度选择view
     * @return view
     */
    private View initQuarterView() {
        return new TextView(this);
    }
    //</editor-fold>

    //初始化按天选择adapter数据
    private Integer[][] createMonth(int count) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -count);

        Integer[][] resultList = new Integer[count+2][2];

        for (int i = 0; i < count + 2; i++) {
            resultList[i][0] = calendar.get(Calendar.YEAR);
            resultList[i][1] = calendar.get(Calendar.MONTH) + 1;
            calendar.add(Calendar.MONTH, 1);
        }
        return resultList;
    }
}
