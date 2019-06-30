package com.ssk.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.ssk.wanandroid.R
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.Color
import androidx.core.content.ContextCompat


/**
 * Created by shisenkun on 2019-06-23.
 */
class ClickableImageView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    AppCompatImageView(context, attrs) {

    override fun setOnClickListener(l: OnClickListener?) {
        setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    setFilter()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    removeFilter()
                }
            }
            false
        }
        super.setOnClickListener(l)
    }

    private fun setFilter() {
        //先获取设置的src图片
        var drawable: Drawable? = drawable
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = background
        }
        drawable?.setColorFilter(ContextCompat.getColor(context, R.color.gray), PorterDuff.Mode.MULTIPLY)
    }

    private fun removeFilter() {
        //先获取设置的src图片
        var drawable: Drawable? = drawable
        //当src图片为Null，获取背景图片
        if (drawable == null) {
            drawable = background
        }
        drawable?.clearColorFilter()
    }

}
