package com.ssk.wanandroid.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.youth.banner.loader.ImageLoader

/**
 * Created by shisenkun on 2019-06-24.
 */
class GlideImageLoader: ImageLoader() {

    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context).load(path).into(imageView)
    }

}