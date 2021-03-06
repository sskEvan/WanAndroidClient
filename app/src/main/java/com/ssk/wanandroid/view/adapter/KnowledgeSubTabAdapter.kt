package com.ssk.wanandroid.view.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.ext.fromHtml

/**
 * Created by shisenkun on 2019-06-25.
 */
class KnowledgeSubTabAdapter(data: List<KnowledgeTabVo>) : BaseQuickAdapter<KnowledgeTabVo, BaseViewHolder>(data) {

    companion object {
        private const val TYPE_HEADER = 1
        private const val TYPE_SUB_TAB = 2
    }

    init {
        multiTypeDelegate = object : MultiTypeDelegate<KnowledgeTabVo>() {
            override fun getItemType(t: KnowledgeTabVo?): Int {
                if (t?.children?.size == 0) return TYPE_SUB_TAB
                else return TYPE_HEADER
            }
        }

        multiTypeDelegate.registerItemType(TYPE_HEADER, R.layout.item_knowledge_sub_tab_header)
            .registerItemType(TYPE_SUB_TAB, R.layout.item_knowledge_sub_tab)
    }

    override fun convert(helper: BaseViewHolder, item: KnowledgeTabVo) {
        when(helper.itemViewType) {
            TYPE_HEADER -> {
                helper.setText(R.id.tvSubProjectHeader, item.name.fromHtml())
            }
            TYPE_SUB_TAB -> {
                helper.setText(R.id.tvSubProject, item.name.fromHtml())
                    .addOnClickListener(R.id.clKnowledgeSubTag)
            }
        }
    }
    
}