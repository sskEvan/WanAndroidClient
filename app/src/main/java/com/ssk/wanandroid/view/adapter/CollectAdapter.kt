package com.ssk.wanandroid.view.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.ext.fromHtml
import com.ssk.wanandroid.widget.CircleTextImageView
import com.ssk.wanandroid.widget.CollectButton

/**
 * Created by shisenkun on 2019-07-27.
 */
class CollectAdapter(data: List<ArticleVo>) : BaseQuickAdapter<ArticleVo, BaseViewHolder>(data) {

    companion object {
        private const val TYPE_COMMON = 1
        private const val TYPE_PROJECT = 2
    }

    init {
        multiTypeDelegate = object : MultiTypeDelegate<ArticleVo>() {
            override fun getItemType(item: ArticleVo): Int {
                if (item.chapterId == 294) return TYPE_PROJECT  //项目分类下的文章
                else return TYPE_COMMON
            }
        }

        multiTypeDelegate.registerItemType(TYPE_COMMON, R.layout.item_article)
            .registerItemType(TYPE_PROJECT, R.layout.item_project)
    }


    override fun convert(helper: BaseViewHolder, item: ArticleVo) {
        when(helper.itemViewType) {
            TYPE_COMMON -> {
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

            TYPE_PROJECT -> {
                helper.setText(R.id.tvAuthor, item.author)
                    .setText(R.id.tvTitle, item.title)
                    .setText(R.id.tvDescription, item.desc.fromHtml())
                    .setText(R.id.tvTime, item.niceDate)
                    .addOnClickListener(R.id.cvItemRoot)
                    .addOnClickListener(R.id.collectButton)

                helper.getView<CollectButton>(R.id.collectButton).setChecked(item.collect)
                Glide.with(mContext).load(item.envelopePic).placeholder(R.mipmap.ic_project_default).into(helper.getView(R.id.imgProject))
                helper.getView<CircleTextImageView>(R.id.ivAuthor).setText4CircleImage(item.author)
            }
        }

    }
}