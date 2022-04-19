package com.yunianshu.library.ui.text

import com.blankj.utilcode.util.GsonUtils
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseFragment
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel
import com.yunianshu.library.adapter.FontAdapter
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.bean.ScrollInfo
import com.yunianshu.library.response.FontResponse
import com.yunianshu.library.util.HttpUtil
import com.yunianshu.library.util.RecycleViewUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class TextFontFragment : BaseFragment() {


    private lateinit var viewModel: TextFontViewModel
    private lateinit var shareViewModel: ShareViewModel
    private lateinit var adapter: FontAdapter
    private var lastSelectPos: Int = 0

    override fun loadView() {
        GlobalScope.launch {
          try {
              loadFontData()

          } catch (e: Exception) {
              e.printStackTrace()
          }
        }
        adapter.setOnItemClickListener { view, item, pos ->
            if(lastSelectPos == 0 && pos != 0){
                viewModel.list.value?.get(lastSelectPos)?.select = false
            }else{
                shareViewModel.textStickerFont.value!!.select = false
            }
            item.select = true
            shareViewModel.textStickerFont.postValue(item)
            viewModel.fontChange.postValue(true)
            viewModel.scrollInfo.postValue(ScrollInfo(pos = pos, step = 1, lastPos = lastSelectPos))
            lastSelectPos = pos
        }
    }

    //获取字体资源
    private suspend fun loadFontData() {
        var result = HttpUtil.request("https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataFontList?page=1&pageSize=0")
        var fontResponse = GsonUtils.fromJson(result, FontResponse::class.java)
        var list = mutableListOf<FontInfo>()
        val element =
            FontInfo(name = "默认字体", url = null, filePath = null, fontImage = null, select = true)
        list.add(element)
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