package com.yunianshu.library.ui.sticker

import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R

/**
 * 5.贴纸界面
 */
class StickerActivity : BaseActivity() {

   private lateinit var viewModel: StickerViewModel

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(StickerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_sticker,BR.vm,viewModel)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }


    inner class StickerClickProxy{

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {

        }
    }

}