package com.yunianshu.library

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.ui.adjust.AdjustmentActivity
import com.yunianshu.library.ui.crop.CropActivity
import com.yunianshu.library.ui.fillter.FillterActivity
import com.yunianshu.library.ui.frame.FrameActivity
import com.yunianshu.library.ui.sticker.StickerActivity
import com.yunianshu.library.ui.words.WordsActivity
import java.io.File


/**
 *
 */
class PhotoEditActivity : BaseActivity() {

    private lateinit var viewModel: PhotoShareViewModel
    private lateinit var adapter: PhotoEditAdapter
    private lateinit var url: String

    /**
     * 1.先初始化ViewModel
     */
    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(PhotoShareViewModel::class.java)
    }

    /**
     * 2.绑定视图
     */
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = PhotoEditAdapter(this)
        return DataBindingConfig(R.layout.activity_photo_edit, BR.vm, viewModel)
            .addBindingParam(BR.click, PhotoEditClickProxy())
            .addBindingParam(BR.adapter, adapter)
    }

    /**
     * 3.填充数据
     */
    override fun loadView() {
        url = intent.getStringExtra("url").toString()
        val parse = Uri.fromFile(File(url))
        findViewById<ImageView>(R.id.imageView).load(parse){
            placeholder(R.drawable.ic_load_default)
        }
        loadPhotoEditItems()
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                if (activityResult.resultCode == Activity.RESULT_OK) {
                    val result = activityResult.data?.getStringExtra("title")
                    result?.let { Log.e("yhj", it) }
                }
            }
        adapter.setOnItemClickListener { _, _, position ->
            var intent: Intent? = null
            when (position) {
                0 -> {
                    intent = Intent(this,AdjustmentActivity::class.java)
                }
                1 -> {
                    intent =Intent(this,FillterActivity::class.java)
                }
                2 -> {
                    intent = Intent(this,CropActivity::class.java)
                }
                3 -> {
                    intent = Intent(this,FrameActivity::class.java)
                }
                4 -> {
                    intent = Intent(this,StickerActivity::class.java)
                }
                5 -> {
                    intent = Intent(this, WordsActivity::class.java)
                }
            }
            intent?.putExtra("url",url)
            activityResultLauncher.launch(intent)
        }
    }

    private fun loadPhotoEditItems() {
        val list = mutableListOf<PhotoEditItem>()
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_adjust, getString(R.string.text_adjust)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_fillter, getString(R.string.text_fillter)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_crop, getString(R.string.text_crop)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_frame, getString(R.string.text_frame)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_sticker, getString(R.string.text_sticker)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_text, getString(R.string.text_text)))
        viewModel.setList(list)
    }

    inner class PhotoEditClickProxy() {

        fun back() {
            finish()
        }

        fun complete() {

        }
    }

}