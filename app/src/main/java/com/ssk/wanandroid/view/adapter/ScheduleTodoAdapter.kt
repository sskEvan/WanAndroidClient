package com.ssk.wanandroid.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.TodoVo

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleTodoAdapter (data: List<TodoVo>) : BaseQuickAdapter<TodoVo, BaseViewHolder>(data) {

    companion object {
        private const val TYPE_HEADER = 1
        private const val TYPE_COMMON = 2
    }

    init {
        multiTypeDelegate = object : MultiTypeDelegate<TodoVo>() {
            override fun getItemType(t: TodoVo?): Int {
                if (t!!.title.isEmpty()) return TYPE_HEADER
                else return TYPE_COMMON
            }
        }

        multiTypeDelegate.registerItemType(TYPE_HEADER, R.layout.item_schedule_todo_header)
            .registerItemType(TYPE_COMMON, R.layout.item_schedule_todo)
    }


    override fun convert(helper: BaseViewHolder, item: TodoVo) {
        if(helper.itemViewType == TYPE_COMMON) {
            helper.setText(R.id.tvTitle, item.title)
            val ivImportantLogo = helper.getView<ImageView>(R.id.ivImportantLogo)
            if(item.priority == 1) {
                ivImportantLogo.visibility = View.VISIBLE
            }else {
                ivImportantLogo.visibility = View.GONE
            }

            val tvType = helper.getView<TextView>(R.id.tvType)
            if(item.type == 0) {
                tvType.visibility = View.GONE
            }else {
                tvType.visibility = View.VISIBLE
                when(item.type) {
                    1 -> {tvType.text = "工作"
                        tvType.setBackgroundResource(R.drawable.bg_todo_type_work)
                    }
                    2 -> {tvType.text = "学习"
                        tvType.setBackgroundResource(R.drawable.bg_todo_type_study)
                    }
                    3 -> {tvType.text = "生活"
                        tvType.setBackgroundResource(R.drawable.bg_todo_type_life)
                    }
                }
            }

            val tvContent = helper.getView<TextView>(R.id.tvContent)
            if(item.content.isEmpty()) {
                tvContent.visibility = View.GONE
            }else {
                tvContent.visibility = View.VISIBLE
                tvContent.setText(item.content)
            }

            helper.addOnClickListener(R.id.cvItemRoot)
            helper.addOnClickListener(R.id.ivEdit)
            helper.addOnLongClickListener(R.id.cvItemRoot)

        }else {
            helper.setText(R.id.tvTodoHeader, item.dateStr)
        }
    }

}