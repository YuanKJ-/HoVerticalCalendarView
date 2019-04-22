package com.hosle.calendar.verticalcalendar.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import com.hosle.calendar.verticalcalendar.R
import com.hosle.calendar.verticalcalendar.util.dp2px
import kotlinx.android.synthetic.main.layout_calendar_view.view.*
import java.util.*


/**
 * Created by tanjiahao on 2018/6/6
 * Original Project VerticalCalendar
 */
class VerticalCalendarView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var dateMonth : Array<Array<Int>> = arrayOf()

    init {
        initView(context)

    }

    private fun initView(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.layout_calendar_view, this, true)
        header_day_of_week.setParams()
        initRecyclerView()
    }

    private fun initRecyclerView() {

        recycler_view_calendar.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            addItemDecoration(MonthSuctionTopDecoration(context)) //插入decoration无侵入式吸顶效果
        }
    }

    private fun isGoingDown(dy: Int): Boolean {
        return dy < 0
    }

    fun setCalendarParams(monthArrange:Array<Array<Int>>,onDayClickListener: MonthView.OnDayClickListener?, primaryColor: Int?, operationForTaskCount:((Calendar) -> Int)? = null) {
        dateMonth = monthArrange
        //刷新完成后自动定位到当前月份
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)
                (recycler_view_calendar.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(dateMonth.size - 2, 0)
            }
        })
        setAdapter(CalendarAdapter(dateMonth, onDayClickListener, primaryColor, operationForTaskCount))
    }

    fun setOndayClickListener(onDayClickListener: MonthView.OnDayClickListener?) {
        (recycler_view_calendar.adapter as CalendarAdapter).onDayClickListener = onDayClickListener
        recycler_view_calendar.adapter.notifyDataSetChanged()
    }

    private fun setAdapter(_adapter: CalendarAdapter) {
        recycler_view_calendar.apply {
            adapter = _adapter
        }
    }


    class CalendarAdapter(val monthArange: Array<Array<Int>>, var onDayClickListener: MonthView.OnDayClickListener?,
                          val primaryColor: Int?, val operationForTaskCount:((Calendar) -> Int)? = null) : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent!!.context).inflate(R.layout.item_single_month, parent, false) as MonthView
            view.setPrimaryColor(primaryColor)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return monthArange.size
        }

        override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
            holder!!.root.setMonthParams(monthArange[position][1], monthArange[position][0], operation = operationForTaskCount)
            holder.root.setOnDayClickListener(onDayClickListener)
        }

    }

    class ViewHolder(val root: MonthView) : RecyclerView.ViewHolder(root)

}