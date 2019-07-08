package com.ssk.wanandroid.widget

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.utils.BitmapUtils


/**
 * Created by shisenkun on 2019-07-07.
 */
class SubProjectDecoration(val context: Context, val datas: List<KnowledgeTabVo>) : RecyclerView.ItemDecoration() {

    private val mPaint by lazy { Paint() }
    private val mLeft = 100F
    private val mBmpTree by lazy {
        BitmapUtils.zoomBitmap(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_tree), 60, 60)
    }

    init {
        mPaint.isAntiAlias = true
        mPaint.style = Paint.Style.STROKE
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        datas.forEach {
            outRect.left = mLeft.toInt()
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val childCount = parent.getChildCount();
        for (i in 0..childCount - 1) {
            val view = parent.getChildAt(i);
            val position = parent.getChildAdapterPosition(view)
            //Log.d("ssk", "i=$i,childCount=$childCount,position=$position")
            if (datas[position].children?.size == 0) {
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
                if(position == datas.size - 1){
                    c.drawLine(
                        20F + mBmpTree.width / 2,
                        view.top.toFloat(),
                        20F + mBmpTree.width / 2,
                        view.top.toFloat() + view.height / 2,
                        mPaint
                    )
                } else if (datas[position + 1].children?.size > 0 && view != null) {
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


}