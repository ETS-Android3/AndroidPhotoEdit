package com.yunianshu.library.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.text.InputFilter
import android.text.TextUtils
import android.view.*
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.KeyboardUtils
import com.blankj.utilcode.util.LogUtils
import com.kunminx.architecture.ui.page.DataBindingActivity
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel
import com.yunianshu.library.adapter.RgAdapter
import com.yunianshu.library.databinding.DialogEditModifyContentBinding
import com.yunianshu.library.ui.text.TextFontFragment
import com.yunianshu.library.ui.text.TextStyleFragment
import com.yunianshu.library.ui.text.TextViewModel

/**
 * @author wanggl
 * @createTime 2022.4.8
 * @description
 */
class ModifyTextContentDialog(context: Context) : Dialog(
    context, R.style.BaseKDialog
) {

    private var mBinding: DialogEditModifyContentBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_edit_modify_content,
            null,
            false
        )
    private val shareVM: ShareViewModel by lazy {
        ViewModelProvider(context as FragmentActivity)[ShareViewModel::class.java]
    }
    private val viewModel: TextViewModel by lazy {
        ViewModelProvider(context as FragmentActivity)[TextViewModel::class.java]
    }
    private var adapter: RgAdapter
    private var onTextChangedListener: OnTextChangedListener? = null
    private var sustained = true

    init {
        mBinding.lifecycleOwner = context as FragmentActivity
        mBinding.vm = viewModel
        mBinding.shareVM = shareVM
        mBinding.click = DialogTextClickProxy()
        adapter = RgAdapter(context)
        adapter.addFragment(TextStyleFragment())
        adapter.addFragment(TextFontFragment())
        mBinding.adapter = adapter
        setContentView(mBinding.root)
        initView()
    }


    fun setOnTextChangedListener(onTextChangedListener: OnTextChangedListener): ModifyTextContentDialog {
        this.onTextChangedListener = onTextChangedListener
        return this
    }

    fun setOnTextChangedListener(
        sustained: Boolean,
        onTextChangedListener: OnTextChangedListener
    ): ModifyTextContentDialog {
        this.onTextChangedListener = onTextChangedListener
        this.sustained = sustained
        return this
    }

    private fun initView() {
        //
        setCanceledOnTouchOutside(true)
        val displayMetrics = context.resources.displayMetrics
        val window = this.window
        if (window != null) {
            val attributes = window.attributes
            attributes.width = displayMetrics.widthPixels
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
            attributes.gravity = Gravity.BOTTOM
            attributes.dimAmount = 0f
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
            window.setWindowAnimations(R.style.EditInputContentDialogAnim)
            window.attributes = attributes
        } else {
            throw NullPointerException("dialog.getWindow() is null")
        }
        //
        mBinding.editIvFinish.setOnClickListener {
            if (!sustained) { //点击确定才返回内容
                if (onTextChangedListener != null) {
                    onTextChangedListener!!.onTextChange(mBinding.editEtInputContent.text.toString())
                }
            }
            KeyboardUtils.hideSoftInput(mBinding.editEtInputContent)
            cancel()
        }
        setOnShowListener { KeyboardUtils.showSoftInput(mBinding.editEtInputContent) }
        mBinding.editEtInputContent.addTextChangedListener(object : CommonTextWatcher() {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (sustained) {
                    if (onTextChangedListener != null) {
                        onTextChangedListener!!.onTextChange(s)
                    }
                }
                //文字改变将弹出输入法
                viewModel.editType.postValue(0)
                KeyboardUtils.showSoftInput(mBinding.editEtInputContent)
            }
        })
        mBinding.editEtInputContent.setOnFocusChangeListener { v, hasFocus ->
            //聚焦将弹出输入法
            if (hasFocus) {
                viewModel.editType.postValue(0)
                KeyboardUtils.showSoftInput(mBinding.editEtInputContent)
            }else{
                KeyboardUtils.hideSoftInput(mBinding.editEtInputContent)
                viewModel.editType.postValue(1)
            }
        }
        mBinding.editEtInputContent.setOnFinishComposingListener(object :ClearEditText.OnFinishComposingListener{
            override fun onFinishComposing(finish:Boolean) {
                if(finish){
                    if(viewModel.editType.value == 0){
                        dismiss()
                    }
                }
            }
        })
        //设置碎片预加载
        mBinding.textViewPager.offscreenPageLimit = 2
    }

    fun setInputType(inputType: Int, inputFilters: Array<InputFilter>): ModifyTextContentDialog {
        mBinding.editEtInputContent.inputType = inputType
        if (inputFilters != null) {
            mBinding.editEtInputContent.filters = inputFilters
        }
        return this
    }

    fun setInputType(inputFilters: Array<InputFilter?>?): ModifyTextContentDialog {
        if (inputFilters != null) {
            mBinding.editEtInputContent.filters = inputFilters
        }
        return this
    }

    override fun dismiss() {
        KeyboardUtils.hideSoftInput(mBinding.editEtInputContent)
        super.dismiss()
    }

    fun setContent(content: String): ModifyTextContentDialog {
        mBinding.editEtInputContent.setText(content)
        if (!TextUtils.isEmpty(content)) {
            try {
                mBinding.editEtInputContent.setSelection(content.length)
            } catch (e: Exception) {
                LogUtils.e(e.message)
            }
        }
        return this
    }

    fun setHint(@StringRes strId: Int): ModifyTextContentDialog {
        mBinding.editEtInputContent.setHint(strId)
        return this
    }

    interface OnTextChangedListener {
        fun onTextChange(charSequence: CharSequence)
    }

    inner class DialogTextClickProxy {

        fun keyboard() {
            viewModel.editType.postValue(0)
            KeyboardUtils.showSoftInput(mBinding.editEtInputContent)
        }

        fun textStyle() {
            KeyboardUtils.hideSoftInput(mBinding.editEtInputContent)
            viewModel.editType.postValue(1)
        }

        fun textFont() {
            KeyboardUtils.hideSoftInput(mBinding.editEtInputContent)
            viewModel.editType.postValue(2)
        }
    }
}