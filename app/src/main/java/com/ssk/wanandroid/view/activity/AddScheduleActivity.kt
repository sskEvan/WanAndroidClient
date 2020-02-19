package com.ssk.wanandroid.view.activity

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanActivity
import com.ssk.wanandroid.bean.ScheduleTypeVo
import com.ssk.wanandroid.event.OnAutoRefreshTodoListEvent
import com.ssk.wanandroid.ext.showToast
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.util.RevealActivityAnimation
import com.ssk.wanandroid.viewmodel.AddScheduleViewModel
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.activity_add_schedule.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by shisenkun on 2019-07-29.
 */
@BindContentView(R.layout.activity_add_schedule)
class AddScheduleActivity : WanActivity<AddScheduleViewModel>() {

    private val mScheduleTypeVoList = mutableListOf(
        ScheduleTypeVo(1, "工作"),
        ScheduleTypeVo(2, "学习"),
        ScheduleTypeVo(3, "生活")
    )

    private val mSdf = SimpleDateFormat("yyyy-MM-dd")
    private val mSdfYear = SimpleDateFormat("yyyy")
    private val mSdfMonth = SimpleDateFormat("MM")
    private val mSdfDay = SimpleDateFormat("dd")

    private var mType = 0
    private var mPriority = 2  //默认一般

    private val mRevealActivityAnimation by lazy {
        RevealActivityAnimation(clRoot, intent, this)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)

        if (savedInstanceState == null) {
            mRevealActivityAnimation.revealActivity()
        }

        setupToolbar(true)
        immersiveStatusBar(R.color.colorPrimary, true)

        setupTypeTabLayout()
        tvDate.text = mSdf.format(Date().time)
        tvDate.setOnClickListener {
            val currentDate = Date()
            DatePickerDialog(
                this, DatePickerDialog.OnDateSetListener { _, year, month, day ->
                    tvDate.text = "${year}-${month + 1}-${day}"
                }, mSdfYear.format(currentDate).toInt(),
                mSdfMonth.format(currentDate).toInt() - 1,
                mSdfDay.format(currentDate).toInt()
            ).show()
        }

        rgPriority.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbImportant -> mPriority = 1
                R.id.rbNormal -> mPriority = 2
            }
        }

        fab.setOnClickListener {
            if (etTitle.text.isEmpty()) {
                showToast("标题不能为空!")
            } else {
                showLoadingDialog("保存中...")
                mViewModel.addSchedule(
                    etTitle.text.toString(),
                    etContent.text.toString(),
                    tvDate.text.toString(),
                    mType,
                    mPriority
                )
            }
        }
    }

    private fun setupTypeTabLayout() {
        typeTabLayout.run {
            adapter = object : TagAdapter<ScheduleTypeVo>(mScheduleTypeVoList) {
                override fun getCount() = mScheduleTypeVoList.size

                override fun getView(parent: FlowLayout, position: Int, t: ScheduleTypeVo): View {
                    val tv = LayoutInflater.from(parent.context).inflate(
                        R.layout.item_schedule_type_tag,
                        parent,
                        false
                    ) as TextView
                    tv.text = t.name
                    return tv
                }
            }

            setOnTagClickListener { _, position, _ ->
                mType = mScheduleTypeVoList[position].type
                true
            }

            mType = intent.getIntExtra("scheduleType", 0)
            if (mType != 0) {
                adapter.setSelectedList(mType - 1)
                adapter.notifyDataChanged()
            }
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.apply {
            mAddScheduleSuccess.observe(this@AddScheduleActivity, androidx.lifecycle.Observer {
                dismissLoadingDialogSuccess("保存成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        EventManager.post(OnAutoRefreshTodoListEvent())
                        onBackPressed()
                    }
                })
            })

            mAddScheduleErrorMsg.observe(this@AddScheduleActivity, androidx.lifecycle.Observer {
                dismissLoadingDialogFailed(it ?: "保存失败")
            })
        }
    }

    override fun onBackPressed() {
        mRevealActivityAnimation.unRevealActivity()
    }

}