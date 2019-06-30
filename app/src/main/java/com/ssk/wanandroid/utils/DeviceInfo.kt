/*
 * Copyright (C) guolin, Suzhou Quxiang Inc. Open source codes for study only.
 * Do not use for commercial purpose.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ssk.wanandroid.utils

import android.content.Context
import android.util.DisplayMetrics
import android.view.ViewConfiguration
import android.view.WindowManager

import com.ssk.wanandroid.App
import com.ssk.wanandroid.AppContext

/**
 * 提供所有与设备相关的信息。
 *
 * @author guolin
 * @since 17/3/30
 */
object DeviceInfo {

    /**
     * 获取当前设备屏幕的宽度，以像素为单位。
     *
     * @return 当前设备屏幕的宽度。
     */
    val screenWidth: Int
        get() {
            val windowManager = AppContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            if (AndroidVersion.hasJellyBeanMR1()) {
                windowManager.defaultDisplay.getRealMetrics(metrics)
            } else {
                windowManager.defaultDisplay.getMetrics(metrics)
            }
            return metrics.widthPixels
        }

    /**
     * 获取当前设备屏幕的高度，以像素为单位。
     *
     * @return 当前设备屏幕的高度。
     */
    val screenHeight: Int
        get() {
            val windowManager = AppContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            if (AndroidVersion.hasJellyBeanMR1()) {
                windowManager.defaultDisplay.getRealMetrics(metrics)
            } else {
                windowManager.defaultDisplay.getMetrics(metrics)
            }
            return metrics.heightPixels
        }

    /**
     * 获取状态栏高度
     */
    val statusBarHeight: Int
        get() {
            if (!ViewConfiguration.get(AppContext).hasPermanentMenuKey()) {
                val name = "status_bar_height"

                return AppContext.resources
                    .getDimensionPixelSize(AppContext.resources.getIdentifier(name, "dimen", "android"))
            }

            return 0
        }

}
