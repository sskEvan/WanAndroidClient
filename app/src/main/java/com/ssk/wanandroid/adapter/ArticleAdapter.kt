package com.ssk.wanandroid.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.widget.CircleTextImageView

/**
 * Created by shisenkun on 2019-06-25.
 */
class ArticleAdapter(layoutResId: Int = R.layout.item_article) : BaseQuickAdapter<ArticleVo, BaseViewHolder>(layoutResId) {

    var showStar = true

    fun showStar(showStar: Boolean) {
        this.showStar = showStar
    }

    override fun convert(helper: BaseViewHolder, item: ArticleVo) {
        var category = "";
        if(item.superChapterName != null && !item.superChapterName.isEmpty()) {
            category += item.superChapterName + "/"
        }
        category += item.chapterName
        helper.setText(R.id.tvAuthor, item.author)
            .setText(R.id.tvTitle, item.title)
            .setText(R.id.tvCategory, category)
            .setText(R.id.tvTime, item.niceDate)
            .addOnClickListener(R.id.ivStar)
            .addOnClickListener(R.id.cvItemRoot)

        helper.getView<CircleTextImageView>(R.id.ivAuthor).setText4CircleImage(item.author)
    }
}