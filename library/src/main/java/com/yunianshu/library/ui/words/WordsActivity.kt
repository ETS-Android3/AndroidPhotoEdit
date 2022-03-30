package com.yunianshu.library.ui.words

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.binding_recyclerview.adapter.BaseDataBindingAdapter
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.R
import com.yunianshu.library.adapter.StickerAdapter
import com.yunianshu.library.adapter.TextStickerAdapter
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.ui.sticker.StickerViewModel
import com.yunianshu.library.view.StickerView
import com.yunianshu.library.view.TextStickerView
import com.yunianshu.library.view.imagezoom.ImageViewTouch

class WordsActivity : BaseActivity(),BaseDataBindingAdapter.OnItemClickListener<StickerInfo> {

    private lateinit var viewModel: WordViewModel
    private lateinit var adapter: TextStickerAdapter
    private lateinit var bitmap: Bitmap
    private lateinit var url:String
    private val imageView: ImageViewTouch by lazy { findViewById(R.id.wordsImage) }
    private val stickerView: TextStickerView by lazy { findViewById(R.id.wordsView) }

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(WordViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = TextStickerAdapter(this)
        adapter.setOnItemClickListener(this)
        return DataBindingConfig(
            R.layout.activity_words,
            BR.vm,
            viewModel
        ).addBindingParam(BR.click, WordsClickProxy())
            .addBindingParam(BR.adapter,adapter)

    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
    }

    inner class WordsClickProxy {

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {

        }
    }

    override fun onItemClick(viewId: Int, item: StickerInfo, position: Int) {

    }
}