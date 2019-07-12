package com.ssk.wanandroid.widget

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.utils.BitmapUtils
import com.ssk.wanandroid.utils.dp2px


/**
 * Created by shisenkun on 2019-07-07.
 */
class SubProjectDecoration(val context: Context, val datas: List<KnowledgeTabVo>) : RecyclerView.ItemDecoration() {

    private val mPaint by lazy { Paint() }
    private val mStickyHeaderPaint by lazy { Paint() }
    private val mLeft = 100F
    private val mBmpTree by lazy {
        BitmapUtils.zoomBitmap(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_tree), 60, 60)
    }

    private var mCurStickyHeaderTabVo: KnowledgeTabVo? = null
    private var mStickyHeaderLayoutHeight = 0
    private var mIsFirst = true
    private var mCurrentParentTabVo: KnowledgeTabVo? = null

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
        mStickyHeaderPaint.isFakeBoldText = true
        mStickyHeaderPaint.textSize = dp2px(15F).toFloat()
        mStickyHeaderPaint.isAntiAlias = true
        mCurStickyHeaderTabVo = datas[0]
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.left = mLeft.toInt()
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            if (mIsFirst && i == 0) {
                mStickyHeaderLayoutHeight = view.height
                mIsFirst = false
            }
            val position = parent.getChildAdapterPosition(view)

            //Log.d("ssk", "i=$i,childCount=$childCount,position=$position")
            if (datas[position].children.isEmpty()) {
                c.drawLine(
                    20F + mBmpTree.width / 2,
                    view.top + view.height / 2F,
                    mLeft - 16,
                    view.top + view.height / 2F,
                    mPaint
                )
                mPaint.color = ContextCompat.getColor(context, R.color.colorPrimary)
                c.drawRect(mLeft - 16, view.top + view.height / 2F - 8, mLeft, view.top + view.height / 2F + 8, mPaint)

                mPaint.color = ContextCompat.getColor(context, R.color.secondary_text)
                if (position == datas.size - 1) {
                    c.drawLine(
                        20F + mBmpTree.width / 2,
                        view.top.toFloat(),
                        20F + mBmpTree.width / 2,
                        view.top.toFloat() + view.height / 2,
                        mPaint
                    )
                } else if (datas[position + 1].children.size > 0 && view != null) {
                    c.drawLine(
                        20F + mBmpTree.width / 2,
                        view.top.toFloat(),
                        20F + mBmpTree.width / 2,
                        view.top.toFloat() + view.height / 2,
                        mPaint
                    )
                } else {
                    c.drawLine(
                        20F + mBmpTree.width / 2,
                        view.top.toFloat(),
                        20F + mBmpTree.width / 2,
                        view.bottom.toFloat(),
                        mPaint
                    )
                }

            } else {
                c.drawBitmap(mBmpTree, 20F, view.top + (view.height - mBmpTree.height.toFloat()) / 2, mPaint)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        c.save()
        //取出第一个子view
        val view = parent.getChildAt(0)
        val position = parent.getChildAdapterPosition(view)
        if (datas[position].children.isEmpty()) {  //子标签
            if (position - 1 > 0 && datas[position - 1].children.isEmpty()) {
                mCurrentParentTabVo = findParentTabVo(datas[position - 1].parentChapterId)
            }
            if (position + 1 < datas.size && datas[position + 1].children.isNotEmpty()) {  //下一个item是其它父标签
                c.translate(0F, 0F)
                mStickyHeaderPaint.color = Color.WHITE
                c.drawRect(
                    0F,
                    view.top.toFloat(),
                    view.right.toFloat(),
                    view.top.toFloat() + mStickyHeaderLayoutHeight,
                    mStickyHeaderPaint
                )
                c.drawBitmap(
                    mBmpTree,
                    20F,
                    view.top + (mStickyHeaderLayoutHeight - mBmpTree.height.toFloat()) / 2,
                    mStickyHeaderPaint
                )
                drawText(c, mCurrentParentTabVo?.name, view.top)
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
                c.drawBitmap(
                    mBmpTree,
                    20F,
                    view.top + (mStickyHeaderLayoutHeight - mBmpTree.height.toFloat()) / 2,
                    mStickyHeaderPaint
                )
                c.restore()
                drawText(c, mCurrentParentTabVo?.name, 0)
            }
        } else {  //父标签
            mCurrentParentTabVo = datas[position]
            c.translate(0F, -view.top.toFloat())
            mStickyHeaderPaint.color = Color.WHITE
            c.drawRect(
                0F,
                view.top.toFloat(),
                view.right.toFloat(),
                view.top.toFloat() + mStickyHeaderLayoutHeight,
                mStickyHeaderPaint
            )
            c.drawBitmap(
                mBmpTree,
                20F,
                view.top + (mStickyHeaderLayoutHeight - mBmpTree.height.toFloat()) / 2,
                mStickyHeaderPaint
            )
            mCurrentParentTabVo = datas[position]
            c.restore()
            drawText(c, mCurrentParentTabVo?.name, 0)
        }

    }

    private fun drawText(c: Canvas, text: String?, offsetY: Int) {
        if (text != null) {
            val textBound = Rect()
            mStickyHeaderPaint.getTextBounds(text, 0, text.length, textBound)
            val targetRect = Rect(
                20 + mBmpTree.width * 3 /2,
                offsetY,
                textBound.width() + mBmpTree.width * 3 /2,
                mStickyHeaderLayoutHeight + offsetY
            )
            val fontMetrics = mStickyHeaderPaint.fontMetricsInt
            val baseline = (targetRect.bottom + targetRect.top - fontMetrics.bottom - fontMetrics.top) / 2
            mStickyHeaderPaint.color = ContextCompat.getColor(context, R.color.primary_text)
            mStickyHeaderPaint.textAlign = Paint.Align.CENTER
            c.drawText(text, targetRect.centerX().toFloat(), baseline.toFloat(), mStickyHeaderPaint)

        }
    }

    private fun findParentTabVo(parentId: Int): KnowledgeTabVo? {
        for (i in 0 until datas.size) {
            if (datas[i].id == parentId) {
                return datas[i]
            }
        }
        return null
    }

}