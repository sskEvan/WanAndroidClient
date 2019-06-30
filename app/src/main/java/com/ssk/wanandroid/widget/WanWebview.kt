package com.ssk.wanandroid.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Created by shisenkun on 2019-06-29.
 */
class WanWebView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null) : WebView(context, attrs) {

    init {
        this.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        initWebViewSettings()
        setClickable(true)
    }

    private fun initWebViewSettings() {
        val webSetting = this.getSettings()
        webSetting.setJavaScriptEnabled(true)
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true)
        webSetting.setAllowFileAccess(true)
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
        webSetting.setSupportZoom(true)
        webSetting.setBuiltInZoomControls(true)
        webSetting.setUseWideViewPort(true)
        webSetting.setSupportMultipleWindows(true)
        webSetting.setLoadWithOverviewMode(true)
        webSetting.setAppCacheEnabled(true)
        webSetting.setDomStorageEnabled(true)
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND)
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE)
    }

}
