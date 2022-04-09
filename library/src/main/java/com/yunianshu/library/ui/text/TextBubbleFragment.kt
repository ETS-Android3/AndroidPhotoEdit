package com.yunianshu.library.ui.text

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Color
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.*
import com.yunianshu.library.adapter.TextStickerAdapter
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.util.AssetsUtil
import com.yunianshu.library.util.ImageUtils.drawableToBitmap
import com.yunianshu.library.util.RecycleViewUtils
import com.yunianshu.library.util.toBean
import com.yunianshu.sticker.TextDrawable
import org.json.JSONObject

class TextBubbleFragment : BaseFragment() {

    private lateinit var viewModel: TextBubbleViewModel
    private lateinit var adapter: TextStickerAdapter
    private lateinit var shareVM: ShareViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun loadView() {
        loadBubble()
        adapter.setOnItemClickListener { _, item, position ->
            shareVM.textStickerInfo.value!!.select = false
            item.select = true
            shareVM.textStickerInfo.postValue(item)
            adapter.notifyDataSetChanged()
            RecycleViewUtils.toPosition(mActivity.findViewById(R.id.recyclerView3), position,viewModel.list.value!!.size,2)
        }
    }

    /**
     * 获取assets气泡文件
     */
    private fun loadBubble() {
        val json = AssetsUtil.getFromAssets(mActivity, "config.json")
        val jsonObject = JSONObject(json)
        val bubbles = jsonObject.getString("bubble")
        val list = bubbles.toBean<List<BubbleInfo>>()

        var infos = mutableListOf<StickerInfo>()
        //默认添加文字 文字边距默认10
        shareVM.textStickerInfo.value?.let {
            infos.add(it)
        }
        //添加assets文件内容
        for (item in list) {
            infos.add(
                StickerInfo(
                    bitmap = BitmapFactory.decodeStream(
                        AssetsUtil.getAssetsFile(
                            mActivity,
                            item.path
                        )
                    ),
                    bubbleInfo = item
                )
            )
        }
        viewModel.list.postValue(infos)
    }

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextBubbleViewModel::class.java)
        shareVM = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = TextStickerAdapter(mActivity)
        return DataBindingConfig(R.layout.fragment_text_bubble, BR.vm,viewModel)
            .addBindingParam(BR.adapter, adapter)
            .addBindingParam(BR.sharevm, shareVM)
    }



}