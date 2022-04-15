package com.yunianshu.library.ui.text

import android.annotation.SuppressLint
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.lxj.xpopup.XPopup
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.indicatorseekbar.widget.SeekParams
import com.yunianshu.library.BR
import com.yunianshu.library.BaseFragment
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel
import com.yunianshu.library.adapter.TextColorAdapter
import com.yunianshu.library.bean.TextColorInfo
import com.yunianshu.library.util.TextColorType
import com.yunianshu.library.view.CustomAttachPopup


class TextStyleFragment : BaseFragment() {


    private lateinit var viewModel: TextStyleViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var adapter: TextColorAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun loadView() {
        initColor()
        adapter.setOnItemClickListener { _, item, pos ->
            if (pos == 0) {
                val bubbleFlag = BubbleFlag(mActivity)
                bubbleFlag.flagMode = FlagMode.FADE
                val colorPickerDialog = ColorPickerDialog.Builder(mActivity)
                    .setTitle("颜色选择器")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton(getString(R.string.base_sure),
                        ColorEnvelopeListener { envelope, _ ->
//                            item.colorString = "#${envelope.hexCode}"
                            adapter.notifyDataSetChanged()
                            if(shareViewModel.textColorType.value == TextColorType.TEXT){
                                shareViewModel.textStickerColor.postValue(TextColorInfo(envelope.color))
                            }else if(shareViewModel.textColorType.value == TextColorType.SHADOW){
                                shareViewModel.textStickerShadowColor.postValue(envelope.color)
                            }

                            viewModel.list.value?.get(1)?.color = envelope.color
                        })
                    .setNegativeButton(
                        getString(R.string.text_back)
                    ) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true) // the default value is true.
                colorPickerDialog.colorPickerView.flagView = bubbleFlag
                colorPickerDialog.setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show()
            } else {
                if(shareViewModel.textColorType.value == TextColorType.TEXT){
                    shareViewModel.textStickerColor.postValue(item)
                }else if(shareViewModel.textColorType.value == TextColorType.SHADOW){
                    shareViewModel.textStickerShadowColor.postValue(item.color)
                }

            }
        }
    }

    private fun initColor() {
        val list = mutableListOf<TextColorInfo>()
        list.add(TextColorInfo(-1, ""))
        resources.getIntArray(R.array.color_primary).forEach {
            list.add(TextColorInfo(it, ""))
        }
        viewModel.list.postValue(list)
    }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextStyleViewModel::class.java)
        shareViewModel = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = TextColorAdapter(mActivity)
        return DataBindingConfig(R.layout.fragment_text_style, BR.vm, viewModel)
            .addBindingParam(BR.sharevm, shareViewModel)
            .addBindingParam(BR.listener, TextStyleColorSeekBarListener())
            .addBindingParam(BR.click, TextStyleClickProxy())
            .addBindingParam(BR.adapter, adapter)
    }

    inner class TextStyleColorSeekBarListener : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams?) {
            shareViewModel.textStickerAlpha.postValue(seekParams?.progress?.toFloat())
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {

        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
        }

    }

    inner class TextStyleClickProxy{

        /**
         * 文字大小
         */
        fun clickBold() {
            shareViewModel.textStickerBold.postValue(!shareViewModel.textStickerBold.value!!)
        }

        /**
         * 文字斜体
         */
        fun clickItalic() {
            shareViewModel.textStickerItalic.postValue(!shareViewModel.textStickerItalic.value!!)
        }

        /**
         * 文字下划线
         */
        fun clickUnderline() {
            shareViewModel.textStickerUnderline.postValue(!shareViewModel.textStickerUnderline.value!!)
        }

        /**
         * 文字阴影
         */
        fun clickShadow() {
            shareViewModel.textStickerShadow.postValue(!shareViewModel.textStickerShadow.value!!)
        }

        /**
         * 文字颜色类型
         */
        fun clickTextType() {
            var idx = 0
            val value = shareViewModel.textColorType.value
            if(value == TextColorType.SHADOW){
                idx = 1
            }
            XPopup.Builder(context)
                .isDarkTheme(false)
                .isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                .asCenterList(
                    "请选择颜色类型", arrayOf("文字颜色", "文字阴影"),
                    null, idx
                ) { position, _ ->
                    when (position) {
                        0 -> shareViewModel.textColorType.postValue(TextColorType.TEXT)
                        1 -> {
                            shareViewModel.textColorType.postValue(TextColorType.SHADOW)
                            shareViewModel.textStickerShadow.postValue(true)
                        }
                    }
                }
                .show()
        }

        /**
         * 文字居左
         */
        fun clickAlignLeft() {
            val value = shareViewModel.textStickerAlignLeft.value!!
            if (value) {
                shareViewModel.textStickerAlignLeft.postValue(false)
                shareViewModel.textStickerAlignCenter.postValue(true)
                shareViewModel.textStickerAlign.postValue(2)
            } else {
                shareViewModel.textStickerAlignLeft.postValue(true)
                shareViewModel.textStickerAlignCenter.postValue(false)
                shareViewModel.textStickerAlign.postValue(0)
            }
            shareViewModel.textStickerAlignRight.postValue(false)
        }

        /**
         * 文字居右
         */
        fun clickAlignRight() {
            val value = shareViewModel.textStickerAlignRight.value!!
            if (value) {
                shareViewModel.textStickerAlign.postValue(2)
                shareViewModel.textStickerAlignRight.postValue(false)
                shareViewModel.textStickerAlignCenter.postValue(true)
            } else {
                shareViewModel.textStickerAlignRight.postValue(true)
                shareViewModel.textStickerAlignCenter.postValue(false)
                shareViewModel.textStickerAlign.postValue(1)
            }
            shareViewModel.textStickerAlignLeft.postValue(false)
        }

        /**
         * 文字居中
         */
        fun clickAlignCenter() {
            val value = shareViewModel.textStickerAlignCenter.value!!
            if (value) {
                shareViewModel.textStickerAlignCenter.postValue(false)
                shareViewModel.textStickerAlignLeft.postValue(true)
                shareViewModel.textStickerAlign.postValue(0)
            } else {
                shareViewModel.textStickerAlign.postValue(2)
                shareViewModel.textStickerAlignCenter.postValue(true)
                shareViewModel.textStickerAlignLeft.postValue(false)
            }
            shareViewModel.textStickerAlignRight.postValue(false)
        }
    }

}