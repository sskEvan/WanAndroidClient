package com.ssk.wanandroid.widget

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.bean.TodoVo
import com.ssk.wanandroid.ext.logDebug
import com.ssk.wanandroid.util.dp2px
import com.ssk.wanandroid.util.sp2px

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleDecoration(val context: Context, val datas: List<TodoVo>) : RecyclerView.ItemDecoration() {

    private val mStickyHeaderPaint by lazy { Paint() }

    private var mCurStickyHeaderTabVo: TodoVo? = null
    private var mStickyHeaderLayoutHeight = 0
    private var mStickyHeaderLayoutPaddingLeft = 0
    private var mIsFirst = true
    private var mCurrentHeaderTodoVo: TodoVo? = null

    init {
        mStickyHeaderPaint.isFakeBoldText = true
        mStickyHeaderPaint.textSize = sp2px(16F).toFloat()
        mStickyHeaderPaint.isAntiAlias = true
        mCurStickyHeaderTabVo = datas[0]
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            if (mIsFirst && i == 0) {
                mStickyHeaderLayoutHeight = view.height
                mStickyHeaderLayoutPaddingLeft = view.paddingLeft
                mIsFirst = false
                return
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        c.save()
        //取出第一个子view
        val view = parent.getChildAt(0)
        val position = parent.getChildAdapterPosition(view)
        if (datas[position].title.isNotEmpty()) {  //普通item
            mCurrentHeaderTodoVo = findHeaderTodoVo(datas[position].date)

            if (position + 1 < datas.size && datas[position + 1].title.isEmpty()) {  //下一个item是其它日期头item

                if (parent.getChildAt(0).height + view.top.toFloat() > mStickyHeaderLayoutHeight) {
                    c.translate(0F, -view.top.toFloat())
                } else {
                    c.translate(0F, parent.getChildAt(0).height - mStickyHeaderLayoutHeight.toFloat())
                }
                mStickyHeaderPaint.color = Color.WHITE
                c.drawRect(
                    0F,
                    view.top.toFloat(),
                    view.right.toFloat(),
                    view.top.toFloat() + mStickyHeaderLayoutHeight,
                    mStickyHeaderPaint
                )
                drawText(c, mCurrentHeaderTodoVo?.dateStr, view.top)
                c.restore()
            } else {
                c.translate(0F, -view.top.toFloat())
                mStickyHeaderPaint.color = Color.WHITE
                c.drawRect(
                    0F,
                    view.top.toFloat(),
                    view.right.toFloat(),
                    view.top.toFloat() + mStickyHeaderLayoutHeight,
                    mStickyHeaderPaint
                )
                c.restore()
                drawText(c, mCurrentHeaderTodoVo?.dateStr, 0)
            }
        } else {  //日期头部item
            mCurrentHeaderTodoVo = datas[position]
            c.translate(0F, -view.top.toFloat())
            mStickyHeaderPaint.color = Color.WHITE
            c.drawRect(
                0F,
                view.top.toFloat(),
                view.right.toFloat(),
                view.top.toFloat() + mStickyHeaderLayoutHeight,
                mStickyHeaderPaint
            )
            c.restore()
            drawText(c, mCurrentHeaderTodoVo?.dateStr, 0)
        }

    }

    private fun drawText(c: Canvas, text: String?, offsetY: Int) {
        if (text != null) {
            val textBound = Rect()
            mStickyHeaderPaint.getTextBounds(text, 0, text.length, textBound)
            val targetRect = Rect(
                mStickyHeaderLayoutPaddingLeft,
                offsetY,
                textBound.width() + mStickyHeaderLayoutPaddingLeft,
                mStickyHeaderLayoutHeight + offsetY
            )
            val fontMetrics = mStickyHeaderPaint.fontMetricsInt
            val baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
            mStickyHeaderPaint.color = ContextCompat.getColor(context, R.color.primary_text)
            mStickyHeaderPaint.textAlign = Paint.Align.CENTER
            c.drawText(text, targetRect.centerX().toFloat(), baseline.toFloat(), mStickyHeaderPaint)
        }
    }

    private fun findHeaderTodoVo(date: Long): TodoVo? {
        for (i in 0 until datas.size) {
            if (datas[i].date == date && datas[i].title.isEmpty()) {
                return datas[i]
            }
        }
        return null
    }

}