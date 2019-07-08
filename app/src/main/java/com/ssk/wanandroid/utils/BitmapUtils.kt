package com.ssk.wanandroid.utils

import android.graphics.Bitmap
import android.graphics.Matrix

/**
 * Created by shisenkun on 2019-07-07.
 */
class BitmapUtils {

    companion object {
        fun zoomBitmap(srcBmp: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
            val width = srcBmp.getWidth().toFloat()
            val height = srcBmp.getHeight().toFloat()
            val matrix = Matrix()
            // 计算宽高缩放率
            val scaleWidth = targetWidth.toFloat() / width
            val scaleHeight = targetHeight.toFloat() / height
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(srcBmp, 0, 0, width.toInt(), height.toInt(), matrix, true)
        }
    }

}