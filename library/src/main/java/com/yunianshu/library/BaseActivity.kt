package com.yunianshu.library

import android.graphics.Color
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kunminx.architecture.ui.page.DataBindingActivity

/**
 * Create by WingGL
 * createTime: 2022/3/21
 */
abstract class BaseActivity:DataBindingActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.TRANSPARENT
        loadView()
    }

    abstract fun loadView()


    protected open fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {

        return ViewModelProvider(this).get(modelClass)
    }

}