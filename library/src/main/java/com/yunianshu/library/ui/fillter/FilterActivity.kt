package com.yunianshu.library.ui.fillter

import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R

/**
 * 2.滤镜处理界面
 */
class FilterActivity : BaseActivity() {
    private lateinit var viewModel: FillterViewModel


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FillterViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
       return DataBindingConfig(R.layout.activity_fillter,BR.vm,viewModel)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }
}