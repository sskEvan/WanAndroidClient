package com.ssk.wanandroid.view.activity

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.webkit.*
import androidx.annotation.RequiresApi
import com.ssk.wanandroid.R
import kotlinx.android.synthetic.main.activity_web.*
import android.net.Uri
import androidx.lifecycle.Observer
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.aspect.annotation.CheckLogin
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.event.OnCollectChangedEvent
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.viewmodel.WanWebViewModel


/**
 * Created by shisenkun on 2019-06-29.
 */
@BindContentView(R.layout.activity_web)
class WanWebActivity : WanActivity<WanWebViewModel>() {

    companion object {
        private const val REQUEST_CODE_COLLECT = 100
    }

    private var mIsNetworkError = true
    private lateinit var mUrl: String
    private lateinit var mTitle: String
    private var mIsCollected = false
    private var mId = 0
    private var mItemCollect: MenuItem? = null

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        switchableConstraintLayout.setRetryListener {
            webView.loadUrl(intent.extras?.getString("url"))
        }

        setupWebView()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mUrl = intent.extras!!.getString("url")!!
        mTitle = intent.extras!!.getString("title")!!
        mId = intent.extras!!.getInt("id")
        mIsCollected = intent.extras!!.getBoolean("isCollected")
        webView.loadUrl(mUrl)
    }

    fun setupWebView() {
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
        toolbar?.title = mTitle
    }

    override fun onDestroy() {
        webView.webViewClient = null
        super.onDestroy()
    }

    override fun startObserve() {
        mViewModel.apply {
            mCollectArticleSuccess.observe(this@WanWebActivity, Observer {
                showSnackBar("收藏成功!")
                mIsCollected = true
                mItemCollect!!.setTitle("取消收藏")
                EventManager.post(OnCollectChangedEvent(true, mId))
            })

            mUnCollectArticleSuccess.observe(this@WanWebActivity, Observer {
                showSnackBar("取消收藏成功!")
                mIsCollected = false
                mItemCollect!!.setTitle("收藏")
                EventManager.post(OnCollectChangedEvent(false, mId))
            })

            mCollectArticleErrorMsg.observe(this@WanWebActivity, Observer {
                showSnackBar(it)
            })

            mUnCollectArticleErrorMsg.observe(this@WanWebActivity, Observer {
                showSnackBar(it)
            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_web, menu)
        val itemCollect = menu.findItem(R.id.menu_item_collect)
        itemCollect.setTitle(if (mIsCollected) "取消收藏" else "收藏")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.menu_item_collect -> {
                mItemCollect = item
                collectArticle()
                return true
            }
            R.id.menu_item_share -> {
                shareArticle()
                return true
            }
            R.id.menu_item_open_by_system_browser -> {
                openBySystemBrowser()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    @CheckLogin(REQUEST_CODE_COLLECT)
    private fun collectArticle() {
        if(mIsCollected) {
            mViewModel.unCollectArticle(mId)
        }else {
            mViewModel.collectArticle(mId)
        }
    }

    private fun shareArticle() {
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, mTitle)
            intent.putExtra(Intent.EXTRA_TEXT, "玩安卓给你分享一偏不错的文章《${mTitle}》,链接：$mUrl")
            startActivity(Intent.createChooser(intent, null))
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun openBySystemBrowser() {
        val uri = Uri.parse(mUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_COLLECT) {
            collectArticle()
        }
    }
}