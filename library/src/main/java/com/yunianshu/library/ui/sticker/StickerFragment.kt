package com.yunianshu.library.ui.sticker

import android.graphics.Bitmap
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.binding_recyclerview.adapter.BaseDataBindingAdapter
import com.yunianshu.library.BR
import com.yunianshu.library.BaseFragment
import com.yunianshu.library.R
import com.yunianshu.library.ShareViewModel
import com.yunianshu.library.adapter.StickerAdapter
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.response.StickerResponse
import com.yunianshu.library.response.Stk
import com.yunianshu.library.util.HttpUtil
import com.yunianshu.library.util.RecycleViewUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * Create by WingGL
 * createTime: 2022/4/12
 */
class StickerFragment : BaseFragment(), BaseDataBindingAdapter.OnItemClickListener<Stk> {

    private lateinit var viewModel: StickerViewModel
    private lateinit var shareVM: ShareViewModel
    private lateinit var adapter: StickerAdapter
    private lateinit var bitmap: Bitmap
    private lateinit var url: String

    override fun loadView() {
        GlobalScope.launch {
            try {
                getHttpData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取网络资源
     */

    private suspend fun getHttpData() {
        val list = mutableListOf<Stk>()
        val response =
            HttpUtil.request("https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataStickerList?page=1&Size=0")
        val fromJson: StickerResponse = GsonUtils.fromJson(response, StickerResponse::class.java)
        fromJson.data.list?.forEach {
            list.add(it)
        }
        viewModel.list.postValue(list)
    }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(StickerViewModel::class.java)
        shareVM = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = StickerAdapter(mActivity)
        adapter.setOnItemClickListener(this)
        return DataBindingConfig(R.layout.fragment_sticker, BR.vm, viewModel)
            .addBindingParam(BR.adapter, adapter)
    }

    override fun onItemClick(viewId: Int, item: Stk, position: Int) {
        thread {
            var bitmap = Glide.with(mActivity)
                .asBitmap()
                .load(item.image)
                .submit(100, 80)
                .get()
            var info = StickerInfo(bitmap = bitmap, bubbleInfo = BubbleInfo())
            shareVM.textStickerInfo.postValue(info)
            RecycleViewUtils.toPosition(
                mActivity.findViewById(R.id.fragment_sticker_recycler_view),
                position,
                viewModel.list.value!!.size,
                2
            )
        }
    }

}