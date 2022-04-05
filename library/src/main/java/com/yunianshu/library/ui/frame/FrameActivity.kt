package com.yunianshu.library.ui.frame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R
import com.yunianshu.library.databinding.ActivityFrameBinding

/**
 * 4.相框调整
 */
class FrameActivity : BaseActivity() {

    private lateinit var viewModel: FrameViewModel
    private val binding:ActivityFrameBinding by lazy { ActivityFrameBinding.inflate(layoutInflater) }

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FrameViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
       return DataBindingConfig(R.layout.activity_frame,BR.vm,viewModel)
           .addBindingParam(BR.click,FrameClickProxy())
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }

    }

    inner class FrameClickProxy {

        fun cancel() {
            finish()
        }
        /**
         * 保存图片
         */
        fun complete() {

        }

    }

}