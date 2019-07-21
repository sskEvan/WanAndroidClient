package com.ssk.wanandroid.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.widget.CircleTextImageView
import com.ssk.wanandroid.widget.CollectButton
import com.ssk.wanandroid.ext.fromHtml

/**
 * Created by shisenkun on 2019-06-25.
 */
class ArticleAdapter(layoutResId: Int = R.layout.item_article) : BaseQuickAdapter<ArticleVo, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: ArticleVo) {
        var category = "";
        if(item.superChapterName != null && !item.superChapterName.isEmpty()) {
            category += item.superChapterName + "/"
        }
        category += item.chapterName

        helper.setText(R.id.tvAuthor, item.author.fromHtml())
            .setText(R.id.tvTitle, item.title.fromHtml())
            .setText(R.id.tvCategory, category.fromHtml())
            .setText(R.id.tvTime, item.niceDate)
            .addOnClickListener(R.id.collectButton)
            .addOnClickListener(R.id.cvItemRoot)

        helper.getView<CollectButton>(R.id.collectButton).setChecked(item.collect)
        helper.getView<CircleTextImageView>(R.id.ivAuthor).setText4CircleImage(item.author)
    }
}