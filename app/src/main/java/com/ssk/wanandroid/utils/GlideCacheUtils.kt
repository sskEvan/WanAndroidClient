package com.ssk.wanandroid.utils

import com.ssk.wanandroid.app.AppContext
import java.io.File
import java.math.BigDecimal
import android.text.TextUtils
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.Glide
import android.os.Looper


/**
 * Created by shisenkun on 2019-07-21.
 */

object GlideCacheUtils {

    /**
     * 清除图片磁盘缓存
     */
    fun clearDiskCache() {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable {
                    Glide.get(AppContext).clearDiskCache()
                }).start()
            } else {
                Glide.get(AppContext).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    fun getCacheSize(): String {
        try {
            val filePath = AppContext.getCacheDir().absolutePath + File.separator + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR
            return getFormatSize(getFolderSize(File(filePath)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList!!) {
                if (aFileList.isDirectory) {
                    size = size + getFolderSize(aFileList)
                } else {
                    size = size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    private fun getFormatSize(size: Double): String {

        val kiloByte = size / 1024
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

}
