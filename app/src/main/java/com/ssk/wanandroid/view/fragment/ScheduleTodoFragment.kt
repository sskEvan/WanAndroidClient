package com.ssk.wanandroid.view.fragment

import android.content.DialogInterface
import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.TodoVo
import com.ssk.wanandroid.event.OnAutoRefreshTodoListEvent
import com.ssk.wanandroid.util.EventManager
import com.ssk.wanandroid.view.activity.EditScheduleActivity
import com.ssk.wanandroid.view.adapter.ScheduleTodoAdapter
import com.ssk.wanandroid.view.dialog.ScheduleOperationDialog
import com.ssk.wanandroid.viewmodel.ScheduleTodoViewModel
import com.ssk.wanandroid.widget.CommonListPager
import com.ssk.wanandroid.widget.ScheduleDecoration
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by shisenkun on 2019-07-28.
 */
class ScheduleTodoFragment : WanFragment<ScheduleTodoViewModel>() {

    companion object {
        const val TYPE_UNRESTRICTED = 0
        const val TYPE_WORK = 1
        const val TYPE_STUDY = 2
        const val TYPE_LIFE = 3

        fun create(type: Int): ScheduleTodoFragment = ScheduleTodoFragment().apply {
            val bundle = Bundle()
            bundle.putInt("type", type)
            arguments = bundle
        }
    }

    private lateinit var commonListPager: CommonListPager<TodoVo>
    private val mAdapter by lazy { ScheduleTodoAdapter(mutableListOf()) }
    private var mType: Int = 0

    override fun getLayoutResId() = R.layout.fragment_schedule_todo

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        mType = arguments!!.getInt("type")
        setupCommonListPager()
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchTodoList(1, mType)
    }

    private fun setupCommonListPager() {
        commonListPager = mContentView.findViewById(R.id.commonListPager)
        commonListPager.setAdapter(mAdapter)
        commonListPager.getRecyclerView().addItemDecoration(ScheduleDecoration(mActivity, mAdapter.data))
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchTodoList(1, mType)
            }

            override fun refresh() {
                mViewModel.fetchTodoList(1, mType)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchTodoList(page + 2, mType)
            }
        }

        mAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{ _, view, position ->
                when (view.id) {
                    R.id.cvItemRoot, R.id.ivEdit -> {
                        val todoVo = mAdapter.data[position]
                        var bundle = Bundle()
                        bundle.putSerializable("todoVo", todoVo)
                        startActivity(EditScheduleActivity::class.java, bundle, true)
                    }
                }
            }

            onItemChildLongClickListener = BaseQuickAdapter.OnItemChildLongClickListener() { _, _, position ->
                val dialog = ScheduleOperationDialog(mActivity, mAdapter.data[position])
                dialog.mSheduleLister = object : ScheduleOperationDialog.ScheduleOperationListener {
                    override fun onComplete(todoVo: TodoVo) {
                        showLoadingDialog("保存中...")
                        mViewModel.completeTodo(todoVo.id, 1)
                        dialog.dismiss()
                    }

                    override fun onDelete(todoVo: TodoVo) {
                        showLoadingDialog("删除中...")
                        mViewModel.deleteTodo(todoVo.id)
                        dialog.dismiss()
                    }
                }
                dialog.show()
                true
            }
        }

    }

    override fun startObserve() {
        super.startObserve()

        mViewModel.apply {
            mTodoListVo.observe(this@ScheduleTodoFragment, Observer {
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
            })

            mFetchTodoListErrorMsg.observe(this@ScheduleTodoFragment, Observer {
                commonListPager.fetchDataError(it)
            })

            mCompleteTodoSuccess.observe(this@ScheduleTodoFragment, Observer {
                dismissLoadingDialogSuccess("保存成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        EventManager.post(OnAutoRefreshTodoListEvent())
                    }
                })
            })

            mCompleteTodoErrorMsg.observe(this@ScheduleTodoFragment, Observer {
                dismissLoadingDialogFailed("保存失败")
            })

            mDeleteTodoSuccess.observe(this@ScheduleTodoFragment, Observer {
                dismissLoadingDialogSuccess("删除成功")
                mLoadingDialog!!.setOnDismissListener(object : DialogInterface.OnDismissListener {
                    override fun onDismiss(dialog: DialogInterface?) {
                        EventManager.post(OnAutoRefreshTodoListEvent())
                    }
                })
            })

            mDeleteTodoErrorMsg.observe(this@ScheduleTodoFragment, Observer {
                dismissLoadingDialogFailed("删除失败")
            })
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: OnAutoRefreshTodoListEvent) {
        commonListPager.autoRefresh()
    }

}