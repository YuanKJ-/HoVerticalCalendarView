package com.hosle.calendar.verticalcalendar.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.hosle.calendar.verticalcalendar.R
import com.hosle.calendar.verticalcalendar.util.dp2px
import java.text.NumberFormat
import java.util.*

/**
 * Created by tanjiahao on 2018/6/6
 * Original Project VerticalCalendarDemo
 */


class MonthView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val DAYS_IN_WEEK = 7
    private val MAX_WEEKS_IN_MONTH = 6 //一个月最大周数是6天

    private var primaryColor: Int = resources.getColor(R.color.calendar_default_primary_color)

    private val calendar = Calendar.getInstance()
    private var daysInMonth = 0
    private var month = 0
    private var year = 0
    private var dayOfWeekStart = 0
    private var weekStart = 0
    private var today = -1

    private var paddedWidth = 0
    private var paddedHeight = 0
    private var cellWidth = 0
    private var cellHeight = 0
    private var monthHeight = resources.getDimension(R.dimen.month_suction_header_height) //顶部label的高度
    private var monthLabelTextSize = resources.getDimension(R.dimen.month_suction_header_text_size) //顶部label的textSize
    private var cellBgSize = context.dp2px(48f) //选中日期蓝色背景size
    private var monthLabelPaddingRight = context.dp2px(10f)

    private val paintCellBg = Paint()
    private val paintCell = TextPaint()
    private val paintMonth = TextPaint()
    private val paintTaskTag = Paint()
    private val dayFormatter: NumberFormat

    private var onDayClickListener: OnDayClickListener? = null

    private var operationForTaskCount: ((Calendar) -> Int)? = null

    init {
        calendar.firstDayOfWeek = Calendar.SUNDAY

        val locale = context.resources.configuration.locale
        dayFormatter = NumberFormat.getIntegerInstance(locale)

        initPaints(context.resources)

    }

    interface OnDayClickListener {
        fun onDayClick(view: MonthView, day: Calendar)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = (event!!.x + 0.5f).toInt()
        val y = (event.y + 0.5f).toInt()

        when (event.action) {
            MotionEvent.ACTION_UP -> run {
                val clickedDay = getDayAtLocation(x, y)
                onDayClicked(clickedDay)
            }
        }

        return true
    }

    fun setMonthParams(_month: Int, _year: Int, _weekStart: Int = Calendar.SUNDAY,operation:((Calendar) -> Int)? = null ) {

        operationForTaskCount = operation

        if (isValidMonth(_month - 1)) {
            month = _month - 1
        }
        year = _year

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        daysInMonth = getDaysInMonth(month, year)
        dayOfWeekStart = calendar.get(Calendar.DAY_OF_WEEK)

        weekStart = if (isValidDayOfWeek(_weekStart)) {
            _weekStart
        } else {
            calendar.firstDayOfWeek
        }

        today = -1
        for (i in 0 until daysInMonth) {
            val day = i + 1
            if (sameDay(day, Calendar.getInstance())) {
                today = day
            }
        }

        requestLayout()
    }

    fun setPrimaryColor(primaryColor: Int?) {
        if (primaryColor != null) {
            this.primaryColor = primaryColor
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val paddedWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        cellWidth = paddedWidth / DAYS_IN_WEEK
        cellHeight = cellWidth

        val preferredHeight = (cellHeight * getActualWeekInMonth()
                + monthHeight
                + paddingTop + paddingBottom).toInt()
        val resolvedHeight = View.resolveSize(preferredHeight, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, resolvedHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val w = right - left
        val h = bottom - top
        val paddedRight = w - paddingRight
        val paddedBottom = h - paddingBottom
        val paddedWidth = paddedRight - paddingLeft
        val paddedHeight = paddedBottom - paddingTop
        if (paddedWidth == this.paddedWidth || paddedHeight == this.paddedHeight) {
            return
        }

        this.paddedWidth = paddedWidth
        this.paddedHeight = paddedHeight

        cellWidth = paddedWidth / DAYS_IN_WEEK
        cellHeight = cellWidth

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMonth(canvas!!)
        drawDays(canvas)

    }

    fun setOnDayClickListener(listener: OnDayClickListener?) {
        onDayClickListener = listener
    }

    fun getYear(): Int {
        return year
    }

    fun getMonth(): Int {
        return month + 1
    }

    private fun onDayClicked(day: Int): Boolean {
        if (!isValidDayOfMonth(day) /*|| !isDayEnabled(day)*/) {
            return false
        }
        // 日期在当天后面,不允许点击
        if (dayAfterToday(day)) {
            return false
        }

        onDayClickListener?.run {
            val date = Calendar.getInstance()
            date.set(year, month, day)
            this.onDayClick(this@MonthView, date)
        }

        return true
    }


    private fun getDayAtLocation(x: Int, y: Int): Int {
        val paddedX = x - paddingLeft
        if (paddedX < 0 || paddedX >= paddedWidth) {
            return -1
        }

        val headerHeight = monthHeight.toInt() /*+ dayOfWeekHeight*/
        val paddedY = y - paddingTop
        if (paddedY < headerHeight || paddedY >= paddedHeight) {
            return -1
        }

        val paddedXRtl: Int
        paddedXRtl = paddedX

        val row = (paddedY - headerHeight) / cellHeight
        val col = paddedXRtl * DAYS_IN_WEEK / paddedWidth
        val index = col + row * DAYS_IN_WEEK
        val day = index + 1 - findDayOffset()
        return if (!isValidDayOfMonth(day)) {
            -1
        } else day

    }

    private fun dayBeforeToday(dayOfMonth: Int): Boolean {
        val today = Calendar.getInstance()

        return when {
            year == today.get(Calendar.YEAR) -> {
                val targetDay = Calendar.getInstance()
                targetDay.set(Calendar.YEAR, year)
                targetDay.set(Calendar.MONTH, month)
                targetDay.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                today.get(Calendar.DAY_OF_YEAR) - targetDay.get(Calendar.DAY_OF_YEAR) > 0
            }
            year < today.get(Calendar.YEAR) -> true
            else -> false
        }
    }

    //获取当前月份实际周数
    private fun getActualWeekInMonth() : Int {
        return calendar.getActualMaximum(Calendar.WEEK_OF_MONTH)
    }

    //判断当前日期是否是周末
    private fun isWeekend(dayOfMonth: Int) : Boolean {
        var cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
        cal.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        return (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY)
    }

    private fun dayAfterToday(dayOfMonth: Int): Boolean {

        if (!sameDay(dayOfMonth, Calendar.getInstance())) {
            return !dayBeforeToday(dayOfMonth)
        }
        return false

    }

    private fun sameDay(day: Int, today: Calendar): Boolean {
        return (year == today.get(Calendar.YEAR) && month == today.get(Calendar.MONTH)
                && day == today.get(Calendar.DAY_OF_MONTH))
    }

    private fun isValidDayOfMonth(day: Int): Boolean {
        return day in 1..daysInMonth
    }

    private fun drawMonth(canvas: Canvas) {
        val monthLabelString = "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月"

        val x = canvas.width / 2 - paintMonth.measureText(monthLabelString) / 2

        // 使文字垂直区域居中绘制
        val fontMetrics = paintMonth.fontMetrics
        val fontHeight = fontMetrics.bottom - fontMetrics.top // 计算文字高度
        val y = monthHeight - (monthHeight - fontHeight) / 2 - fontMetrics.bottom // 计算文字baseline 

        canvas.drawText(monthLabelString, x, y, paintMonth)
    }

    private fun drawDays(canvas: Canvas) {
        val paintText = paintCell
        val paintTag = paintTaskTag

        val colWidth = cellWidth

        var col = findDayOffset()
        val rowHeight = cellHeight
        var rowCenter = rowHeight / 2 + monthHeight
        val halfLineHeight = (paintText.ascent() + paintText.descent()) / 2f


        for (day in 1..daysInMonth) {
            val colCenter = colWidth * col + colWidth / 2
            val colCenterRtl = colCenter

            val dayString: String

            when {
            //todo 选中样式
//                day == today -> {
//                    dayString = "今天"
//                    paintText.color = resources.getColor(android.R.color.white)
//                    paintTag.color = resources.getColor(R.color.calendar_color_blue_ABFF)
//                    //选中日期画蓝色背景
//                    canvas.drawRect(colCenter - cellBgSize / 2f, rowCenter - cellBgSize / 2f,
//                            colCenter + cellBgSize / 2f, rowCenter + cellBgSize / 2f, paintCellBg)
//                }
                dayAfterToday(day) -> {
                    dayString = day.toString()
                    paintText.color = resources.getColor(R.color.calendar_color_99_white)
                    paintTag.color = primaryColor
                }
                isWeekend(day) -> {
                    dayString = day.toString()
                    paintText.color = primaryColor
                    paintTag.color = primaryColor
                }
                else -> {
                    dayString = day.toString()
                    paintText.color = resources.getColor(R.color.calendar_color_white)
                    paintTag.color = resources.getColor(R.color.calendar_color_66_white)
                }
            }

            canvas.drawText(dayString, colCenterRtl.toFloat(), rowCenter - halfLineHeight, paintText)
            drawTaskTag(canvas, colCenterRtl.toFloat(), rowCenter - halfLineHeight, day, paintTag)

            if (++col == DAYS_IN_WEEK) {
                col = 0
                rowCenter += rowHeight
            }
        }

    }

    private fun drawTaskTag(canvas: Canvas, x: Float, y: Float, day: Int, paint: Paint) {
        val margin = context.dp2px(10f)
        val radius = context.dp2px(2f).toFloat()
        val padding = context.dp2px(1f).toFloat()

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val taskCount: Int = operationForTaskCount?.invoke(calendar) ?: 0

        when (taskCount) {
            0 -> {
            }
            1 -> {
                canvas.drawCircle(x, y + margin, radius, paint)
            }
            2 -> {
                canvas.drawCircle(x + radius + padding / 2, y + margin, radius, paint)
                canvas.drawCircle(x - radius - padding / 2, y + margin, radius, paint)
            }
            else -> {
                canvas.drawCircle(x, y + margin, radius, paint)
                canvas.drawCircle(x - 2 * radius - padding, y + margin, radius, paint)
                canvas.drawCircle(x + 2 * radius + padding, y + margin, radius, paint)
            }
        }
    }

    private fun findDayOffset(): Int {
        val offset = dayOfWeekStart - weekStart
        return if (dayOfWeekStart < weekStart) {
            offset + DAYS_IN_WEEK
        } else offset
    }

    private fun isValidDayOfWeek(day: Int): Boolean {
        return day >= Calendar.SUNDAY && day <= Calendar.SATURDAY
    }

    private fun getDaysInMonth(month: Int, year: Int): Int {
        return when (month) {
            Calendar.JANUARY, Calendar.MARCH, Calendar.MAY, Calendar.JULY, Calendar.AUGUST, Calendar.OCTOBER, Calendar.DECEMBER -> 31
            Calendar.APRIL, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.NOVEMBER -> 30
            Calendar.FEBRUARY -> if (year % 4 == 0) 29 else 28
            else -> throw IllegalArgumentException("Invalid Month")
        }
    }

    private fun isValidMonth(month: Int): Boolean {
        return month >= Calendar.JANUARY && month <= Calendar.DECEMBER
    }

    private fun initPaints(res: Resources) {
        paintMonth.isAntiAlias = true
        paintMonth.textSize = monthLabelTextSize
        paintMonth.textAlign = Paint.Align.LEFT
        paintMonth.color = resources.getColor(R.color.calendar_color_white)

        paintCell.isAntiAlias = true
        // 默认黑色,周末时间设置为白色
        paintCell.color = resources.getColor(R.color.calendar_color_white)
        paintCell.textSize = context.dp2px(16f).toFloat()
        paintCell.textAlign = Paint.Align.CENTER

        paintCellBg.isAntiAlias = true
        // 默认蓝色,周末时间设置为白色
        paintCellBg.color = primaryColor

        paintTaskTag.color = primaryColor
        paintTaskTag.isAntiAlias = true
    }


}