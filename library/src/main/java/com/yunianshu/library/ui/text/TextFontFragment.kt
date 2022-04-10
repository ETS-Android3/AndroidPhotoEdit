package com.yunianshu.library.ui.text

import com.blankj.utilcode.util.GsonUtils
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseFragment
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel
import com.yunianshu.library.adapter.FontAdapter
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.response.FontResponse
import com.yunianshu.library.util.HttpUtil
import com.yunianshu.library.util.RecycleViewUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TextFontFragment : BaseFragment() {


    private lateinit var viewModel: TextFontViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var adapter: FontAdapter

    override fun loadView() {
        GlobalScope.launch {
          try {
              loadFontData()

          } catch (e: Exception) {
              e.printStackTrace()
          }
        }
        adapter.setOnItemClickListener { view, item, pos ->
            shareViewModel.textStickerFont.value!!.select = false
            item.select = true
            shareViewModel.textStickerFont.postValue(item)
            adapter.notifyDataSetChanged()
            mActivity?.let {
                RecycleViewUtils.toPosition(mActivity.findViewById(R.id.fontRecycleView),pos,viewModel.list.value!!.size,1)
            }
        }
    }

    //获取字体资源
    private suspend fun loadFontData() {
        var result = HttpUtil.request("https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataFontList?page=1&pageSize=0")
        var fontResponse = GsonUtils.fromJson(result, FontResponse::class.java)
        var list = mutableListOf<FontInfo>()
        list.add(shareViewModel.textStickerFont.value!!)
        fontResponse.data.list.forEach {
            list.add(FontInfo(name = it.font_name,url = it.file,filePath = null, type = 2, fontImage = it.image))
        }
        viewModel.list.postValue(list)
    }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextFontViewModel::class.java)
        shareViewModel = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FontAdapter(mActivity)
        return DataBindingConfig(R.layout.fragment_text_font,BR.vm,viewModel)
            .addBindingParam(BR.sharevm,shareViewModel)
            .addBindingParam(BR.adapter,adapter)
    }
}