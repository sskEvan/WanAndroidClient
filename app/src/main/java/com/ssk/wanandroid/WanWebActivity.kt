package com.ssk.wanandroid

import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.annotation.RequiresApi
import com.ssk.wanandroid.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*

/**
 * Created by shisenkun on 2019-06-29.
 */
class WanWebActivity : BaseActivity() {

    private var mIsNetworkError = true;

    override fun getLayoutId() = R.layout.activity_web

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
                    mIsNetworkError = false
                }

                override fun onPageFinished(p0: WebView?, p1: String?) {
                    super.onPageFinished(p0, p1)
                    if (!mIsNetworkError) {
                        switchableConstraintLayout.switchSuccessLayout()
                    }
                }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                    super.onReceivedError(view, request, error)

                    if(error?.errorCode == -2) {  //网络错误
                        mIsNetworkError = true
                        switchableConstraintLayout.switchFailedLayout("网络错误,请检查网络连接")
                    }

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