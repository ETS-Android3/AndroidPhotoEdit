package com.yunianshu.library.ui.text

import androidx.viewpager2.widget.ViewPager2
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.*
import com.yunianshu.library.adapter.RgAdapter

class TextFragment : BaseFragment() {

    private lateinit var viewModel: TextViewModel
    private lateinit var shareVM: ShareViewModel
    private lateinit var adapter: RgAdapter
    private val viewPager: ViewPager2 by lazy {   mActivity.findViewById(R.id.viewPager22) }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextViewModel::class.java)
        shareVM = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = RgAdapter(mActivity)
        return DataBindingConfig(R.layout.fragment_text, BR.vm, viewModel)
            .addBindingParam(BR.adapter, adapter)
            .addBindingParam(BR.click, TextClickProxy())
    }

    override fun loadView() {
        adapter.addFragment(TextBubbleFragment())
        adapter.addFragment(TextStyleFragment())
        adapter.addFragment(TextFontFragment())
        viewModel.editType.postValue(0)
    }


    inner class TextClickProxy {

        fun textBubble() {
            viewModel.editType.postValue(0)
            viewPager.currentItem = 0
        }

        fun textStyle() {
            viewModel.editType.postValue(1)
            viewPager.currentItem = 1
        }

        fun textFont() {
            viewModel.editType.postValue(2)
            viewPager.currentItem = 2
        }

    }

}