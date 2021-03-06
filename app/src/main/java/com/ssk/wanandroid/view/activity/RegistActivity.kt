package com.ssk.wanandroid.view.activity

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.app.WanAndroid
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.viewmodel.LoginViewModel
import kotlinx.android.synthetic.main.activity_regist.*

/**
 * Created by shisenkun on 2019-06-17.
 */
@BindContentView(R.layout.activity_regist)
class RegistActivity : WanActivity<LoginViewModel>() {

    private lateinit var userName: String
    private lateinit var passWord: String
    private lateinit var confirmPassword : String

    override fun initView(savedInstanceState: Bundle?) {
        setupToolbar(true)
        supportActionBar?.setHomeAsUpIndicator(R.mipmap.ic_close)
        immersiveStatusBar(R.color.colorPrimary, true)
    }

    override fun initData(savedInstanceState: Bundle?) {
        btnRegist.setOnClickListener {
            regist()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_regist, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.menu_item_login -> {
                startActivity(LoginActivity::class.java)
                WanAndroid.uiHandler.postDelayed({finish()}, 300)
            }
        }
        return true
    }

    override fun doEnterAnim() {
        overridePendingTransition(
            R.anim.slide_bottom_in,
            R.anim.slide_bottom_none
        )
    }

    override fun doExitAnim() {
        overridePendingTransition(
            R.anim.slide_bottom_none,
            R.anim.slide_bottom_out
        )
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mRegisterUser.observe(this@RegistActivity, Observer {
                dismissLoadingDialog()
                autoLogin()
            })

            mRegistErrorMsg.observe(this@RegistActivity, Observer {
                dismissLoadingDialogFailed(it ?: "注册失败")
            })

            mLoginUser.observe(this@RegistActivity, Observer {
                dismissLoadingDialogSuccess("登陆成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        WanAndroid.currentUserJson = Gson().toJson(it)
                        WanAndroid.currentUser = it
                        finish()
                        doExitAnim()
                    }
                })
            })

            mLoginErrorMsg.observe(this@RegistActivity, Observer {
                dismissLoadingDialogFailed(it ?: "登陆失败")
            })
        }
    }

    private fun regist() {
        if (checkInput()) {
            showLoadingDialog("注册中...")
            mViewModel.regist(userName, passWord)
        }
    }

    private fun checkInput(): Boolean {
        userName = etUsername.text.toString()
        passWord = etPassword.text.toString()
        confirmPassword = etConfirmPassword.text.toString()
        if (userName.isEmpty()) {
            showToast("请输入用户名")
            return false
        }
        if (passWord.isEmpty()) {
            showToast("请输入密码")
            return false
        }
        if(!confirmPassword.equals(passWord)) {
            showToast("前后密码不一致")
            return false
        }
        WanAndroid.username = userName
        WanAndroid.password = passWord

        return true
    }

    private fun autoLogin() {
        WanAndroid.username = userName
        WanAndroid.password = passWord
        showLoadingDialog("登陆中...")
        mViewModel.login(userName, passWord)
    }

}