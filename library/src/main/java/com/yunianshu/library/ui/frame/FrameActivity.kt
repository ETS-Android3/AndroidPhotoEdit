package com.yunianshu.library.ui.frame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R

/**
 * 4.相框调整
 */
class FrameActivity : BaseActivity() {

    private lateinit var viewModel: FrameViewModel


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FrameViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
       return DataBindingConfig(R.layout.activity_frame,BR.vm,viewModel)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }
}