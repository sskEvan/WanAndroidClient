package com.ssk.wanandroid.fragment

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.ssk.wanandroid.KnowledgeDetailActivity
import com.ssk.wanandroid.R
import com.ssk.wanandroid.base.WanFragment
import com.ssk.wanandroid.bean.KnowledgeTabVo
import com.ssk.wanandroid.adapter.KnowledgeSubTabAdapter
import com.ssk.wanandroid.adapter.KnowledgeTagAdapter
import com.ssk.wanandroid.viewmodel.KnowledgeViewModel
import com.ssk.wanandroid.widget.SubProjectDecoration
import kotlinx.android.synthetic.main.fragment_knowledge.*


/**
 * Created by shisenkun on 2019-06-23.
 */

class KnowledgeFragment : WanFragment<KnowledgeViewModel>() {

    companion object {
        fun create(): KnowledgeFragment {
            return KnowledgeFragment()
        }
    }

    private val mKnowledgeTabAdapter by lazy { KnowledgeTagAdapter() }
    private var mKnowledgeSubTabAdapter: KnowledgeSubTabAdapter? = null
    private lateinit var rvKnowledgeTabLayoutManager: LinearLayoutManager
    private lateinit var rvKnowledgeSubTabLayoutManager: LinearLayoutManager
    private lateinit var mKnowledgeTagVoList: List<KnowledgeTabVo>
    private lateinit var mKnowledgeSubTagVoList: List<KnowledgeTabVo>
    private var mScrollingPosition = 0
    private var mIsScrolling = false

    override fun getLayoutResId() = R.layout.fragment_knowledge

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            immersiveStatusBar(R.color.colorPrimary, true)
            animateToolbarTitle()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        setupToolbar(false)
        toolbar?.title = "知识体系"
        initRv()
        switchableConstraintLayout.setRetryListener {
            mViewModel.fetchKnowledgeTagList()
        }
    }

    private fun initRv() {
        rvKnowledgeTabLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvKnowledgeSubTabLayoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        rvKnowledgeTag.run {
            layoutManager = rvKnowledgeTabLayoutManager
            adapter = mKnowledgeTabAdapter
        }

        rvKnowledgeSubTag.run {
            layoutManager = rvKnowledgeSubTabLayoutManager

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    Log.d("ssk", "newState=$newState")
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {  //进行第二次滑动
                        if (!mIsScrolling) {
                            val firstPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                            if (mKnowledgeSubTagVoList[firstPosition].children.size > 0) {
                                val position = mKnowledgeTagVoList.indexOf(mKnowledgeSubTagVoList[firstPosition])
                                mKnowledgeTabAdapter.updateSelectedPosition(position)
                                rvKnowledgeTag.smoothScrollToPosition(position)
                            } else {
                                val lastPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                                if(lastPosition == mKnowledgeSubTagVoList.size - 1) {
                                    for(i in 0..mKnowledgeTagVoList.size - 1) {
                                        if(mKnowledgeTagVoList[i].id == mKnowledgeSubTagVoList[firstPosition].parentChapterId) {
                                            mKnowledgeTabAdapter.updateSelectedPosition(Math.max(i, mKnowledgeTabAdapter.mLastSeletedPosition))
                                            rvKnowledgeTag.smoothScrollToPosition(Math.max(i, mKnowledgeTabAdapter.mLastSeletedPosition))
                                        }
                                    }
                                }else {
                                    for(i in 0..mKnowledgeTagVoList.size - 1) {
                                        if(mKnowledgeTagVoList[i].id == mKnowledgeSubTagVoList[firstPosition].parentChapterId) {
                                            mKnowledgeTabAdapter.updateSelectedPosition(i)
                                            rvKnowledgeTag.smoothScrollToPosition(i)
                                        }
                                    }
                                }
                            }
                        }
                        smoothScrollToPositionAndTopSecondIfNeeded(recyclerView, mScrollingPosition)
                    }
                }
            })
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        super.initData(savedInstanceState)
        mViewModel.fetchKnowledgeTagList()
    }

    override fun startObserve() {
        super.startObserve()
        mViewModel.apply {
            mKnowledgeTabVoList.observe(this@KnowledgeFragment, Observer {
                switchableConstraintLayout.switchSuccessLayout()
                setKnowledgeTabs(it)
            })

            mKnowledgeSubTabVoList.observe(this@KnowledgeFragment, Observer {
                setKnowledgeSubTabs(it)
            })

            mFetchKnowledgeTabListErrorMsg.observe(this@KnowledgeFragment, Observer {
                switchableConstraintLayout.switchFailedLayout()
            })
        }
    }

    private fun setKnowledgeTabs(knowledgeTagVoList: List<KnowledgeTabVo>) {
        mKnowledgeTagVoList = knowledgeTagVoList
        mKnowledgeTagVoList[0].selected = true
        mKnowledgeTabAdapter.run {
            addData(mKnowledgeTagVoList)

            onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.clKnowledgeTag -> {
                        mKnowledgeTabAdapter.updateSelectedPosition(position)
                        for (i in 0..mKnowledgeSubTagVoList.size - 1) {
                            if (mKnowledgeSubTagVoList[i].id == mKnowledgeTagVoList[position].id) {
                                smoothScrollToPositionAndTop(rvKnowledgeSubTabLayoutManager, i)
                                break
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * 将rv平移到指定位置,并且置顶
     */
    fun smoothScrollToPositionAndTop(layoutManager: LinearLayoutManager, position: Int) {
        mScrollingPosition = position
        val firstItem = layoutManager.findFirstVisibleItemPosition()
        val lastItem = layoutManager.findLastVisibleItemPosition()
        if (position <= firstItem) {  //当要置顶的项在当前显示的第一个项的前面时
            rvKnowledgeSubTag.smoothScrollToPosition(position)
        } else if (position <= lastItem) {  //当要置顶的项已经在屏幕上显示时
            val topOffset = rvKnowledgeSubTag.getChildAt(position - firstItem).top
            rvKnowledgeSubTag.smoothScrollBy(0, topOffset)
        } else {  //当要置顶的项在当前显示的最后一项的后面时
            rvKnowledgeSubTag.smoothScrollToPosition(position)
            mIsScrolling = true
        }
    }

    /**
     * 将rv平移到指定位置,并且置顶  二次滑动
     */
    fun smoothScrollToPositionAndTopSecondIfNeeded(recyclerView: RecyclerView, scrollPosition: Int) {
        if (mIsScrolling) {
            mIsScrolling = false
            val firstPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
            val offsetPosition = scrollPosition - firstPosition
            if (0 <= offsetPosition && offsetPosition < recyclerView.childCount) {
                //获取要置顶的项顶部离RecyclerView顶部的距离
                val topOffset = recyclerView.getChildAt(offsetPosition).top
                //最后的移动
                recyclerView.smoothScrollBy(0, topOffset)
            }
        }
    }

    private fun setKnowledgeSubTabs(knowledgeSubTagVoList: List<KnowledgeTabVo>) {
        mKnowledgeSubTagVoList = knowledgeSubTagVoList
        mKnowledgeSubTabAdapter = KnowledgeSubTabAdapter(mKnowledgeSubTagVoList)
        mKnowledgeSubTabAdapter!!.onItemChildClickListener =
            BaseQuickAdapter.OnItemChildClickListener { _, view, position ->
                when (view.id) {
                    R.id.clKnowledgeSubTag -> {
                        val bundle = Bundle()
                        bundle.putInt("knowledgeId", knowledgeSubTagVoList[position].id)
                        bundle.putString("title", knowledgeSubTagVoList[position].name)
                        startActivity(KnowledgeDetailActivity::class.java, bundle)
                        mActivity.overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_none)
                    }
                }
            }
        rvKnowledgeSubTag.adapter = mKnowledgeSubTabAdapter
        rvKnowledgeSubTag.addItemDecoration(SubProjectDecoration(mActivity, knowledgeSubTagVoList))
    }

}