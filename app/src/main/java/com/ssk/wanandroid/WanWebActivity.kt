package com.ssk.wanandroid

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.annotation.RequiresApi
import com.ssk.wanandroid.base.BaseActivity
import com.ssk.wanandroid.widget.SwitchableConstraintLayout
import kotlinx.android.synthetic.main.activity_article_detail.*

/**
 * Created by shisenkun on 2019-06-29.
 */
class WanWebActivity : BaseActivity() {

    private var mIsReceivedError = true;

    override fun getLayoutId() = R.layout.activity_article_detail

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        switchableConstraintLayout.setRetryListener {
            webView.loadUrl(intent.extras?.getString("url"))
        }

        initWebView()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        webView.loadUrl(intent.extras?.getString("url"))
    }

    fun initWebView() {
        webView.run {
            webViewClient = object : WebViewClient() {

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    mIsReceivedError = false
                }

                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    if (!mIsReceivedError) {
                        switchableConstraintLayout.switchSuccessLayout()
                    }
                }

                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)
                    mIsReceivedError = true
                    switchableConstraintLayout.switchFailedLayout()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbar?.title = intent.extras?.getString("title")
    }

    override fun onDestroy() {
        webView.webViewClient = null
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

}