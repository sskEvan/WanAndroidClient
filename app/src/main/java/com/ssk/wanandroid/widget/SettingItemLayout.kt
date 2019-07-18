package com.ssk.wanandroid.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.layout_setting_item.view.*

/**
 * Created by shisenkun on 2019-07-18.
 */
class SettingItemLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : ConstraintLayout(context, attrs) {

    private var showDivider = false
    private var name: String? = null
    private var logoResId = -1

    init {
        View.inflate(context, R.layout.layout_setting_item, this)
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {

        setBackgroundResource(R.drawable.bg_setting_item)

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.SettingItemLayout)
            showDivider = a.getBoolean(R.styleable.SettingItemLayout_show_divider, false)
            name = a.getString(R.styleable.SettingItemLayout_name)
            logoResId = a.getResourceId(R.styleable.SettingItemLayout_logo, logoResId)
            a.recycle()
        }

        divider.visibility = if (showDivider) View.VISIBLE else View.GONE
        if(name != null && name!!.isNotEmpty()) {
            tvName.text = name
        }
        if(logoResId != -1) {
            ivLogo.setImageResource(logoResId)
        }
    }

}