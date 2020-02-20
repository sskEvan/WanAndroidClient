package com.ssk.wanandroid.aspect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.ssk.wanandroid.app.AppContext

/**
 * Created by shisenkun on 2020-02-20.
 */
class TestActivity : AppCompatActivity() {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val fm = supportFragmentManager
        var index = requestCode shr 16
        if (index != 0) {
            index--
            if (fm.fragments == null || index < 0
                || index >= fm.fragments.size
            ) {

                return
            }
            val frag = fm.fragments[index]
            if (frag == null) {

            } else {
                handleResult(frag, requestCode, resultCode, data)
            }
            return
        }

    }

    /**
     * 递归调用，对所有子Fragement生效
     *
     * @param frag
     * @param requestCode
     * @param resultCode
     * @param data
     */
    private fun handleResult(
        frag: Fragment, requestCode: Int, resultCode: Int,
        data: Intent?
    ) {
        frag.onActivityResult(requestCode and 0xffff, resultCode, data)
        val frags = frag.childFragmentManager.fragments
        if (frags != null) {
            for (f in frags) {
                if (f != null)
                    handleResult(f, requestCode, resultCode, data)
            }
        }
    }
}
