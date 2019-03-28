package com.wehotel.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hosle.calendar.verticalcalendar.view.MonthView;
import com.hosle.calendar.verticalcalendar.view.VerticalCalendarView;
import com.hosle.vertical_calendar.demo.R;
import com.wehotel.calendar.adapter.CalendarPagerAdapter;
import com.wehotel.calendar.bean.WhCalendarBean;
import com.wehotel.calendar.enums.TimeTypeEnum;
import com.wehotel.calendar.listener.DateSelectCallback;
import com.wehotel.calendar.util.TimeFormatUtils;
import com.wehotel.calendar.util.XStatusBarCompat;
import com.wehotel.calendar.view.MonthSelectView;
import com.wehotel.calendar.view.SeasonSelectView;
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

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import qiu.niorgai.StatusBarCompat;

/**
 * Created by kejie.yuan
 * Date: 2019/3/14
 * Description: 日历选择通用Activity
 */
public class V3CalendarActivity extends AppCompatActivity implements View.OnClickListener, DateSelectCallback{

    private static final String EXTRA_PRIMARY_COLOR = "primary_color";
    private static final int INIT_YEAR = 2015;
    private int primaryColor;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private View backIcon;
    private View yesterdayBtn; //直接选定昨天
    private View thisWeekBtn; //直接选定本周
    private View thisMonthBtn; //直接选定本月

    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_v3);
        initPrimaryColor();
        initBtn();
        initViewPager();
        initMagicIndicator();
        XStatusBarCompat.setStatusBarColor(this, Color.WHITE);
        XStatusBarCompat.changeToLightStatusBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void initPrimaryColor() {
        primaryColor = getIntent().getIntExtra("primary_color", getResources().getColor(R.color.calendar_default_primary_color));
    }

    private void initBtn() {
        backIcon = findViewById(R.id.back_icon);
        yesterdayBtn = findViewById(R.id.yesterday_btn);
        thisWeekBtn = findViewById(R.id.this_week_btn);
        thisMonthBtn = findViewById(R.id.this_month_btn);
        backIcon.setOnClickListener(this);
        yesterdayBtn.setOnClickListener(this);
        thisWeekBtn.setOnClickListener(this);
        thisMonthBtn.setOnClickListener(this);
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<View> views = new ArrayList<>();
        views.add(initDayView());
        views.add(initWeekView());
        views.add(initMonthView());
        views.add(initSeasonView());
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
                indicator.setColors(primaryColor);
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
        final VerticalCalendarView calendarView = new VerticalCalendarView(this);
        Single.create(new SingleOnSubscribe<Integer[][]>() {
            @Override
            public void subscribe(SingleEmitter<Integer[][]> e) throws Exception {
                Integer[][] dayData = TimeFormatUtils.getDayData(INIT_YEAR);
                e.onSuccess(dayData);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer[][]>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer[][] dayData) {
                        calendarView.setCalendarParams(dayData, new MonthView.OnDayClickListener() {
                            @Override
                            public void onDayClick(@NotNull MonthView view, @NotNull Calendar day) {
                                String dateString = day.get(Calendar.YEAR) +
                                        "-" + (day.get(Calendar.MONTH) + 1) + "-" + day.get(Calendar.DAY_OF_MONTH);
                                onDateSelect(day.getTimeInMillis(), day.getTimeInMillis(), TimeTypeEnum.DAY.getType(), dateString, dateString);
                            }
                        }, primaryColor, null);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        return calendarView;
    }

    /**
     * 初始化按周选择view
     * @return view
     */
    private View initWeekView() {
        WeekSelectView weekSelectView =  new WeekSelectView(this);
        weekSelectView.setDateSelectCallback(this);
        weekSelectView.generateDataSync(compositeDisposable);
        return weekSelectView;
    }

    /**
     * 初始化按月选择view
     * @return view
     */
    private View initMonthView() {
        MonthSelectView monthSelectView = new MonthSelectView(this);
        monthSelectView.setDateSelectCallback(this);
        monthSelectView.generateDataSync(compositeDisposable);
        return monthSelectView;
    }

    /**
     * 初始化按季度选择view
     * @return view
     */
    private View initSeasonView() {
        SeasonSelectView seasonSelectView = new SeasonSelectView(this);
        seasonSelectView.setDateSelectCallback(this);
        seasonSelectView.generateDataSync(compositeDisposable);
        return seasonSelectView;
    }
    //</editor-fold>

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back_icon) {
            finish();
        } else if (v.getId() == R.id.yesterday_btn) {
            // 获取昨天时间戳和格式化字符串
            String lastDayFormat = TimeFormatUtils.getLastDay();
            long beginDate = TimeFormatUtils.formatTimeToMill(lastDayFormat);
            onDateSelect(beginDate, beginDate, TimeTypeEnum.DAY.getType(), lastDayFormat, lastDayFormat);
        } else if (v.getId() == R.id.this_week_btn) {
            // 获取本周开始和结束的时间戳和格式化字符串
            long beginDate = TimeFormatUtils.formatTimeToMill(TimeFormatUtils.getWeekFirstDay());
            long endDate = TimeFormatUtils.formatTimeToMill(TimeFormatUtils.getWeekEndDay());
            String beginFormat = TimeFormatUtils.getCurrentYear()+"-"+TimeFormatUtils.getWeekOfCurrentYear();
            onDateSelect(beginDate,endDate, TimeTypeEnum.WEEK.getType(), beginFormat,beginFormat);
        } else if (v.getId() == R.id.this_month_btn) {
            // 获取本月开始和结束的时间戳和格式化字符串
            long beginDate = TimeFormatUtils.formatTimeToMill(TimeFormatUtils.getFirstDayOfMonth());
            long endDate = TimeFormatUtils.formatTimeToMill(TimeFormatUtils.getEndDayOfMonth());
            String beginFormat = TimeFormatUtils.getThisMonth();
            onDateSelect(beginDate,endDate, TimeTypeEnum.MONTH.getType(),beginFormat,beginFormat);
        }
    }

    @Override
    public void onDateSelect(long beginDate, long endDate, String type, String beginFormat, String endFormat) {
        // 构造WhCalendarBean
        WhCalendarBean whCalendarBean = new WhCalendarBean();
        whCalendarBean.setBeginDate(beginDate);
        whCalendarBean.setEndDate(endDate);
        whCalendarBean.setType(type);
        whCalendarBean.setBeginFormat(beginFormat);
        whCalendarBean.setEndFormat(endFormat);

        Toast.makeText(this, beginFormat + "~" + endFormat, Toast.LENGTH_SHORT).show();

        // 构造返回intent
//        Intent intent = new Intent();
//        intent.putExtra(WhCalendarBean.class.getName(), whCalendarBean);
//        setResult(RESULT_OK, intent);
//        finish();
    }
}