package com.ssk.wanandroid

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.ssk.wanandroid.app.App
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_login.*

/**
 * Created by shisenkun on 2019-06-17.
 */
class LoginActivity : WanActivity<LoginViewModel>() {

    private lateinit var userName: String
    private lateinit var passWord: String

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar(true)
        supportActionBar?.setHomeAsUpIndicator(R.mipmap.ic_close)
        immersiveStatusBar(R.color.colorPrimary, true)
    }

    override fun initData(savedInstanceState: Bundle?) {
        etUsername.setText(WanAndroid.username)
        etPassword.setText(WanAndroid.password)
        btnLogin.setOnClickListener {
            login()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_item_regist -> {
                startActivity(RegistActivity::class.java)
                WanAndroid.uiHandler.postDelayed({finish()}, 300)
            }
        }
        return true
    }

    override fun doEnterAnim() {
        overridePendingTransition(R.anim.slide_bottom_in, R.anim.slide_bottom_none)
    }

    override fun doExitAnim() {
        overridePendingTransition(R.anim.slide_bottom_none, R.anim.slide_bottom_out)
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mLoginUser.observe(this@LoginActivity, Observer {
                WanAndroid.currentUser = it
                WanAndroid.currentUserJson = Gson().toJson(it)
                dismissLoadingDialogSuccess("登陆成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        finish()
                        doExitAnim()
                    }
                })
            })

            mLoginErrorMsg.observe(this@LoginActivity, Observer {
                dismissLoadingDialogFailed(it ?: "登陆失败")
            })

        }
    }

    private fun login() {
        if (checkInput()) {
            showLoadingDialog("登陆中...")
            mViewModel.login(userName, passWord)
        }
    }

    private fun checkInput(): Boolean {
        userName = etUsername.text.toString()
        passWord = etPassword.text.toString()
        if (userName.isEmpty()) {
            showToast("请输入用户名")
            return false
        }
        if (passWord.isEmpty()) {
            showToast("请输入密码")
            return false
        }
        WanAndroid.username = userName
        WanAndroid.password = passWord

        return true
    }

}