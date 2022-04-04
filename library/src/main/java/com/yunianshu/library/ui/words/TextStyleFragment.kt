package com.yunianshu.library.ui.words

import com.kunminx.architecture.ui.page.DataBindingConfig
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


class TextStyleFragment : BaseFragment() {


    private lateinit var viewModel: TextStyleViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var adapter:TextColorAdapter

    override fun loadView() {
        initColor()
        adapter.setOnItemClickListener { _, item, pos ->
            if(pos == 0){
                val bubbleFlag = BubbleFlag(mActivity)
                bubbleFlag.flagMode = FlagMode.FADE
                var colorPickerDialog = ColorPickerDialog.Builder(mActivity)
                    .setTitle("ColorPicker Dialog")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton(getString(R.string.base_sure),
                        ColorEnvelopeListener { envelope, _ ->
                            shareViewModel.textStickerColor.postValue(TextColorInfo(envelope.color))
                        })
                    .setNegativeButton(
                        getString(R.string.text_back)
                    ) { dialogInterface, i -> dialogInterface.dismiss() }
                    .attachAlphaSlideBar(true) // the default value is true.
                    .attachBrightnessSlideBar(true) // the default value is true.
                    colorPickerDialog.colorPickerView.flagView = bubbleFlag
                    colorPickerDialog.setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                    .show()
            }else{
                shareViewModel.textStickerColor.postValue(item)
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
        return DataBindingConfig(R.layout.fragment_text_style,BR.vm,viewModel)
            .addBindingParam(BR.sharevm,shareViewModel)
            .addBindingParam(BR.listener,TextStyleColorSeekBarListener())
            .addBindingParam(BR.click,TextStyleClickProxy())
            .addBindingParam(BR.adapter,adapter)
    }

    inner class TextStyleColorSeekBarListener: OnSeekChangeListener {

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
        fun clickBold(){
            shareViewModel.textStickerBold.postValue(!shareViewModel.textStickerBold.value!!)
        }
        /**
         * 文字斜体
         */
        fun clickItalic(){
            shareViewModel.textStickerItalic.postValue(!shareViewModel.textStickerItalic.value!!)
        }
        /**
         * 文字下划线
         */
        fun clickUnderline(){
            shareViewModel.textStickerUnderline.postValue(!shareViewModel.textStickerUnderline.value!!)
        }
        /**
         * 文字居左
         */
        fun clickAlignLeft(){
           shareViewModel.textStickerAlign.postValue(0)
        }
        /**
         * 文字居右
         */
        fun clickAlignRight(){
            shareViewModel.textStickerAlign.postValue(1)
        }
        /**
         * 文字居中
         */
        fun clickAlignCenter(){
            shareViewModel.textStickerAlign.postValue(2)
        }



    }

}