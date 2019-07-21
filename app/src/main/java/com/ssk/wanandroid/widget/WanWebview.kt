package com.ssk.wanandroid.widget

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

    @Suppress("DEPRECATION")
    private fun initWebViewSettings() {
        settings.apply {
            setJavaScriptEnabled(true)
            setJavaScriptCanOpenWindowsAutomatically(true)
            setAllowFileAccess(true)
            setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
            setSupportZoom(true)
            setBuiltInZoomControls(true)
            setUseWideViewPort(true)
            setSupportMultipleWindows(true)
            setLoadWithOverviewMode(true)
            setAppCacheEnabled(true)
            setDomStorageEnabled(true)
            setGeolocationEnabled(true)
            setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
            pluginState = WebSettings.PluginState.ON_DEMAND
            setCacheMode(WebSettings.LOAD_NO_CACHE)
        }
    }

}
