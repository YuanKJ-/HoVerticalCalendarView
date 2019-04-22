package com.hosle.calendar.verticalcalendar.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import com.hosle.calendar.verticalcalendar.R

/**
 * 垂直日历view - 无侵入式吸顶label
 */
class MonthSuctionTopDecoration(val context: Context) : RecyclerView.ItemDecoration() {

    private val res: Resources = context.resources

    private val headerHeight = res.getDimension(R.dimen.month_suction_header_height)
    private val textSize = res.getDimension(R.dimen.month_suction_header_text_size)

    private val paint: Paint = Paint() //背景颜色paint
    private val textPaint: TextPaint = TextPaint() //文字paint

    // 文字绘制基准点y
    var y: Float = 0f

    init {
        paint.color = res.getColor(R.color.calendar_color_bg)
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = res.getColor(R.color.calendar_color_white)

        // 使文字垂直区域居中绘制
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.bottom - fontMetrics.top // 计算文字高度
        y = headerHeight - (headerHeight - fontHeight) / 2 - fontMetrics.bottom // 计算文字baseline
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)

        // 文字绘制基准点y
        var actualY = y

        if(parent.childCount >= 2) {
            // 获取列表可见位置的第一第二个item index
            val firstIndex = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val secondIndex = firstIndex + 1

            val firstView = (parent.layoutManager as LinearLayoutManager).findViewByPosition(firstIndex) as MonthView?
            val secondView = (parent.layoutManager as LinearLayoutManager).findViewByPosition(secondIndex) as MonthView?

            val text = "${firstView?.getYear()}年${firstView?.getMonth()}月"
            // 如果第一个item和第二个item年份不相等,decoration需要跟随上移,否则固定
            if (firstView?.getMonth() != secondView?.getMonth()) {
                var topOffset = 0f
                if (secondView != null && secondView.top <= headerHeight) {
                    topOffset = headerHeight - secondView.top
                    actualY -= topOffset
                }
                c.drawRect(0f, -topOffset, c.width.toFloat(), headerHeight - topOffset, paint)
                c.drawText(text, parent.width / 2f, actualY, textPaint)
            } else {
                c.drawRect(0f, 0f, c.width.toFloat(), headerHeight, paint)
                c.drawText(text, parent.width / 2f, actualY, textPaint)
            }
        }

    }
}