package com.yunianshu.library

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kunminx.architecture.ui.page.DataBindingFragment

/**
 * Create by WingGL
 * createTime: 2022/4/1
 * 说明:继承子类方法调用顺序
 * 1.initViewModel->getFragmentScopeViewModel、getActivityScopeViewModel
 * 2.getDataBindingConfig
 * 3.loadView
 */
abstract class BaseFragment : DataBindingFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadView()
    }

    abstract fun loadView()


    protected open fun <T : ViewModel> getFragmentScopeViewModel(modelClass: Class<T>): T {

        return ViewModelProvider(this).get(modelClass)
    }

    protected open fun <T : ViewModel> getActivityScopeViewModel(modelClass: Class<T>): T {

        return ViewModelProvider(mActivity).get(modelClass)
    }
}