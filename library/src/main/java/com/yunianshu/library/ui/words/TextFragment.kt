package com.yunianshu.library.ui.words

import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.Utils
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.*
import com.yunianshu.library.adapter.StickerAdapter
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.util.AssetsUtil
import com.yunianshu.library.util.ImageUtils
import com.yunianshu.library.util.toBean
import com.yunianshu.library.view.ModifyContentDialog
import com.yunianshu.sticker.Sticker
import com.yunianshu.sticker.StickerView
import com.yunianshu.sticker.TextDrawable
import com.yunianshu.sticker.TextSticker
import org.json.JSONObject

class TextFragment : BaseFragment() {

    private lateinit var viewModel: TextViewModel
    private lateinit var shareVM: ShareViewModel
    private lateinit var adapter: StickerAdapter

    override fun initViewModel() {
        viewModel = getFragmentScopeViewModel(TextViewModel::class.java)
        shareVM = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = StickerAdapter(mActivity)
        return DataBindingConfig(R.layout.fragment_text, BR.vm, viewModel)
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        loadBubble()
        adapter.setOnItemClickListener { _, item, _ ->
            shareVM.textStickerInfo.postValue(item)
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
        infos.add(
            StickerInfo(
                bitmap = drawableToBitmap(
                    TextDrawable.builder()
                        .beginConfig()
                        .width(100)
                        .height(100)
                        .endConfig()
                        .buildRound("文字", Color.parseColor("#82D0E7"))
                ),
                bubbleInfo = BubbleInfo(
                    type = Contant.STICKER_TYPE_TEXT,
                    paddingLeft = 10,
                    paddingBottom = 10,
                    paddingRight = 10,
                    paddingTop = 10
                )
            )
        )
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

    private fun drawableToBitmap(drawable: TextDrawable): Bitmap {
        val config =
            if (drawable.getOpacity() != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
        val createBitmap =
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, config)
        val canvas = Canvas(createBitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return createBitmap
    }

}