package com.ssk.wanandroid.view.fragment

import android.os.Bundle
import androidx.lifecycle.Observer
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.TodoVo
import com.ssk.wanandroid.view.adapter.ScheduleTodoAdapter
import com.ssk.wanandroid.viewmodel.ScheduleTodoViewModel
import com.ssk.wanandroid.widget.CommonListPager
import com.ssk.wanandroid.widget.ScheduleDecoration

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
        commonListPager.commonListPagerListener = object : CommonListPager.CommonListPagerListener {
            override fun retry() {
                mViewModel.fetchTodoList(1, mType)
            }

            override fun refresh() {
                mViewModel.fetchTodoList(1, mType)
            }

            override fun loadMore(page: Int) {
                mViewModel.fetchTodoList(page, mType)
            }
        }

        mAdapter.run {
            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener{ _, view, position ->
                when (view.id) {

                }
            }
        }

    }

    override fun startObserve() {
        super.startObserve()

        mViewModel.apply {
            mTodoListVo.observe(this@ScheduleTodoFragment, Observer {
                commonListPager.addData(it.datas)
                commonListPager.setNoMoreData(it.over)
                commonListPager.getRecyclerView().addItemDecoration(ScheduleDecoration(mActivity, mAdapter.data))
            })

            mFetchTodoListErrorMsg.observe(this@ScheduleTodoFragment, Observer {
                commonListPager.fetchDataError(it)
            })
        }
    }

}