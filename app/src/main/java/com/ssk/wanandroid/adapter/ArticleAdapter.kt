package com.ssk.wanandroid.adapter

import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.widget.CircleTextImageView
import com.ssk.wanandroid.widget.CollectButton

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
        helper.setText(R.id.tvAuthor, Html.fromHtml(item.author))
            .setText(R.id.tvTitle, Html.fromHtml(item.title))
            .setText(R.id.tvCategory, Html.fromHtml(category))
            .setText(R.id.tvTime, item.niceDate)
            .addOnClickListener(R.id.collectButton)
            .addOnClickListener(R.id.cvItemRoot)

        helper.getView<CollectButton>(R.id.collectButton).setChecked(item.collect)
        helper.getView<CircleTextImageView>(R.id.ivAuthor).setText4CircleImage(item.author)
    }
}