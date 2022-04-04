package com.yunianshu.library.ui.words

import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseFragment
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel

class TextFontFragment : BaseFragment() {


    private lateinit var viewModel: TextFontViewModel
    private lateinit var shareViewModel: ShareViewModel

    override fun loadView() {
    }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextFontViewModel::class.java)
        shareViewModel = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_text_font,BR.vm,viewModel)
            .addBindingParam(BR.sharevm,shareViewModel)
    }
}