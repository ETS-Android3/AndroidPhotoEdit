package com.yunianshu.library.ui.words

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R

class WordsActivity : BaseActivity() {

    private lateinit var viewModel: WordViewModel

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(WordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_words,BR.vm,viewModel)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }
}