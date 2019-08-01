package com.ssk.wanandroid.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.ssk.wanandroid.R
import com.youth.banner.loader.ImageLoader

/**
 * Created by shisenkun on 2019-06-24.
 */
class BannerGlideImageLoader : ImageLoader() {

    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context)
            .load(path)
            .placeholder(R.mipmap.ic_banner_default)
            .error(R.mipmap.ic_banner_default)
            .into(imageView)
    }

}