package com.ykj.calendar.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import com.hosle.vertical_calendar.demo.R
import com.ykj.calendar.adapter.BaseValueAdapter

class SuctionTopDecoration(val context: Context) : RecyclerView.ItemDecoration() {

    private val res: Resources = context.resources

    private val headerHeight = res.getDimension(R.dimen.calendar_value_header_height)
    private val textSize = res.getDimension(R.dimen.calendar_value_header_text_size)
    private val textPaddingLeft = res.getDimension(R.dimen.calendar_value_header_padding_left)

    private val paint: Paint = Paint() //背景颜色paint
    private val textPaint: TextPaint = TextPaint() //文字paint

    // 文字绘制基准点x,y
    var x: Float = 0f
    var y: Float = 0f

    init {
        paint.color = res.getColor(R.color.calendar_value_header_bg_color)
        textPaint.isAntiAlias = true
        textPaint.textSize = textSize
        textPaint.color = res.getColor(R.color.calendar_color_99_white)
        textPaint.textAlign = Paint.Align.LEFT

        x = textPaddingLeft

        // 使文字垂直区域居中绘制
        val fontMetrics = textPaint.fontMetrics
        val fontHeight = fontMetrics.bottom - fontMetrics.top // 计算文字高度
        y = headerHeight - (headerHeight - fontHeight) / 2 - fontMetrics.bottom // 计算文字baseline
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDrawOver(c, parent, state)

        // 文字绘制基准点y
        var actualY = y

        if(parent.childCount > 2) {
            // 获取列表可见位置的第一第二个item index
            val firstIndex = (parent.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val secondIndex = firstIndex + 1

            val firstView = (parent.layoutManager as LinearLayoutManager).findViewByPosition(firstIndex)
            val secondView = (parent.layoutManager as LinearLayoutManager).findViewByPosition(secondIndex)

            val valueAdapter = parent.adapter as BaseValueAdapter<*, *>
            val firstValueBean = valueAdapter.getItem(firstIndex)
            val secondValueBean = valueAdapter.getItem(secondIndex)

            val text = "${firstValueBean?.year}年"
            // 如果第一个item和第二个item年份不相等,decoration需要跟随上移,否则固定
            if (firstValueBean?.year != secondValueBean?.year) {
                var topOffset = 0f
                if (secondView != null && secondView.top <= headerHeight) {
                    topOffset = headerHeight - secondView.top
                    actualY -= topOffset
                }
                c.drawRect(0f, -topOffset, c.width.toFloat(), headerHeight - topOffset, paint)
                c.drawText(text, x, actualY, textPaint)
            } else {
                c.drawRect(0f, 0f, c.width.toFloat(), headerHeight, paint)
                c.drawText(text, x, actualY, textPaint)
            }
        }

    }
}