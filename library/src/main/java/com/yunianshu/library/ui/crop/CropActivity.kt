package com.yunianshu.library.ui.crop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R
/**
 * 3.裁剪界面
 */
class CropActivity : BaseActivity() {
    private lateinit var viewModel: CropViewModel

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(CropViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
       return DataBindingConfig(R.layout.activity_crop,BR.vm,viewModel)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }
}