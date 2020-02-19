package com.ssk.wanandroid.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.lib_annotation.annotation.BindContentView
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.ScheduleVo
import com.ssk.wanandroid.event.OnAutoRefreshTodoListEvent
import com.ssk.wanandroid.ext.logDebug
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.view.activity.EditScheduleActivity
import com.ssk.wanandroid.view.activity.ScheduleDetailActivity
import com.ssk.wanandroid.view.adapter.ScheduleAdapter
import com.ssk.wanandroid.view.dialog.ScheduleOperationDialog
import com.ssk.wanandroid.viewmodel.ScheduleViewModel
import com.ssk.wanandroid.widget.CommonListPager
import com.ssk.wanandroid.widget.ScheduleDecoration
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-28.
 */
@BindContentView(R.layout.fragment_schedule)
class ScheduleFragment : WanFragment<ScheduleViewModel>() {

    companion object {
        const val TYPE_UNRESTRICTED = 0
        const val TYPE_WORK = 1
        const val TYPE_STUDY = 2
        const val TYPE_LIFE = 3

        fun create(type: Int, status: Int): ScheduleFragment = ScheduleFragment().apply {
            val bundle = Bundle()
            bundle.putInt("type", type)
            bundle.putInt("status", status)
            arguments = bundle
        }
    }

    private lateinit var commonListPager: CommonListPager<ScheduleVo>
    private val mAdapter by lazy { ScheduleAdapter(mutableListOf()) }
    private var mType: Int = 0
    private var mStatus: Int = 0

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mType = arguments!!.getInt("type")
        mStatus = arguments!!.getInt("status")
        setupCommonListPager()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchScheduleList(1, mType, mStatus)
    }

    private fun setupCommonListPager() {
        commonListPager = mContentView.findViewById(R.id.commonListPager)
        commonListPager.setAdapter(mAdapter)
        commonListPager.getRecyclerView().addItemDecoration(ScheduleDecoration(mActivity, mAdapter.data))
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchScheduleList(1, mType, mStatus)
            }

            override fun refresh() {
                mViewModel.fetchScheduleList(1, mType, mStatus)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchScheduleList(page + 1, mType, mStatus)
            }
        }

        mAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{ _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot -> {
                        val scheduleVo = mAdapter.data[position]
                        var bundle = Bundle()
                        bundle.putSerializable("scheduleVo", scheduleVo)
                        if(mStatus == 1) {  //已完成
                            startActivity(ScheduleDetailActivity::class.java, bundle, true)
                        }else {
                            startActivity(EditScheduleActivity::class.java, bundle, true)
                        }
                    }
                    R.id.ivEdit -> {
                        val dialog = ScheduleOperationDialog(mActivity, mAdapter.data[position])
                        dialog.mSheduleLister = object : ScheduleOperationDialog.ScheduleOperationListener {
                            override fun onComplete(scheduleVo: ScheduleVo) {
                                showLoadingDialog("保存中...")
                                mViewModel.completeTodo(scheduleVo.id, if(scheduleVo.status == 1) 0 else 1)
                                dialog.dismiss()
                            }

                            override fun onDelete(scheduleVo: ScheduleVo) {
                                showLoadingDialog("删除中...")
                                mViewModel.deleteTodo(scheduleVo.id)
                                dialog.dismiss()
                            }
                        }
                        dialog.show()
                    }
                }
            }
        }

    }

    override fun startObserve() {
        super.startObserve()

        mViewModel.apply {
            mScheduleListVo.observe(this@ScheduleFragment, Observer {
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mFetchScheduleListErrorMsg.observe(this@ScheduleFragment, Observer {
                commonListPager.fetchDataError(it)
            })

            mCompleteScheduleSuccess.observe(this@ScheduleFragment, Observer {
                dismissLoadingDialogSuccess("保存成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        EventManager.post(OnAutoRefreshTodoListEvent())
                    }
                })
            })

            mCompleteScheduleErrorMsg.observe(this@ScheduleFragment, Observer {
                dismissLoadingDialogFailed("保存失败")
            })

            mDeleteScheduleSuccess.observe(this@ScheduleFragment, Observer {
                dismissLoadingDialogSuccess("删除成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        EventManager.post(OnAutoRefreshTodoListEvent())
                    }
                })
            })

            mDeleteScheduleErrorMsg.observe(this@ScheduleFragment, Observer {
                dismissLoadingDialogFailed("删除失败")
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnAutoRefreshTodoListEvent) {
        logDebug("ssk", "接收到OnAutoRefreshTodoListEvent")
        commonListPager.autoRefresh()
    }

}