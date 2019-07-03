package com.ssk.wanandroid.fragment.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ssk.wanandroid.R
import com.ssk.wanandroid.bean.ArticleVo
import com.ssk.wanandroid.widget.CircleTextImageView

/**
 * Created by shisenkun on 2019-06-25.
 */
class ProjectAdapter(layoutResId: Int = R.layout.item_project) : BaseQuickAdapter<ArticleVo, BaseViewHolder>(layoutResId) {

    override fun convert(helper: BaseViewHolder, item: ArticleVo) {

        helper.setText(R.id.tvAuthor, item.author)
            .setText(R.id.tvTitle, item.title)
            .setText(R.id.tvDescription, item.desc)
            .setText(R.id.tvTime, item.niceDate)
            .addOnClickListener(R.id.cvItemRoot)


        Glide.with(mContext).load(item.envelopePic).into(helper.getView(R.id.imgProject))
        helper.getView<CircleTextImageView>(R.id.ivAuthor).setText4CircleImage(item.author)
    }
}