package com.ssk.wanandroid.view.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.ext.fromHtml

/**
 * Created by shisenkun on 2019-06-25.
 */
class KnowledgeTagAdapter(layoutResId: Int = R.layout.item_knowledge_tag) : BaseQuickAdapter<KnowledgeTabVo, BaseViewHolder>(layoutResId) {

    var mLastSeletedPosition = 0

    override fun convert(helper: BaseViewHolder, item: KnowledgeTabVo) {
        helper.setText(R.id.tvKnowledgeTag, item.name.fromHtml())
            .addOnClickListener(R.id.clKnowledgeTag)

        helper.getView<ImageView>(R.id.ivSelectedLabel).visibility = if(item.selected) View.VISIBLE else View.GONE
        helper.getView<TextView>(R.id.tvKnowledgeTag).isSelected = item.selected
    }

    fun updateSelectedPosition(positon: Int) {
        if(mLastSeletedPosition != positon) {
            mData[mLastSeletedPosition].selected = false
            mData[positon].selected = true
            notifyDataSetChanged()
            mLastSeletedPosition = positon
        }
    }

}