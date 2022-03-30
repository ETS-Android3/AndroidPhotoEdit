package com.yunianshu.library

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.UriUtils
import com.blankj.utilcode.util.Utils
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.ui.adjust.AdjustmentActivity
import com.yunianshu.library.ui.crop.CropActivity
import com.yunianshu.library.ui.fillter.FilterActivity
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
    private lateinit var filePath: String
    private var rotate: Boolean = false
    private var width: Int = 0
    private var height: Int = 0

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
        filePath = intent.getStringExtra("url").toString()
        rotate = intent.getBooleanExtra("rotate", false)
        width = intent.getIntExtra("width", 0)
        height = intent.getIntExtra("height", 0)
        loadImage()
        loadPhotoEditItems()
        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
                val uri = activityResult.data
                uri?.let {
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(
                        BitmapFactory.decodeFile(
                            UriUtils.uri2File(uri.data).toString()
                        )
                    )
                    filePath = UriUtils.uri2File(uri.data).absolutePath
                }
            }
        adapter.setOnItemClickListener { _, _, position ->
            var intent: Intent? = null
            when (position) {
                Contant.ADJUST -> {
                    intent = Intent(this, AdjustmentActivity::class.java)
                }
                Contant.FILTER -> {
                    intent = Intent(this, FilterActivity::class.java)
                }
                Contant.CROP -> {
                    intent = Intent(this, CropActivity::class.java)
                }
                Contant.FRAME -> {
                    intent = Intent(this, FrameActivity::class.java)
                }
                Contant.STICKER -> {
                    intent = Intent(this, StickerActivity::class.java)
                }
                Contant.WORDS -> {
                    intent = Intent(this, WordsActivity::class.java)
                }
            }
            intent?.putExtra("url", filePath)
            activityResultLauncher.launch(intent)
        }
    }

    private fun loadImage() {
        var bitmap = BitmapFactory.decodeFile(filePath)
        if (rotate) {//是否需要旋转
            bitmap = ImageUtils.rotate(
                bitmap,
                90,
                bitmap.width.toFloat(),
                bitmap.height.toFloat()
            )
            var path = Utils.getApp()
                .getExternalFilesDir("tmp")!!.absolutePath + File.separator + "rotate_" + com.yunianshu.library.util.ImageUtils.hashCode(
                filePath
            ) + ".jpg"
            val save = ImageUtils.save(bitmap, path, Bitmap.CompressFormat.JPEG)
            if (!save) {
                Log.e(this.localClassName, "保存失败")
            } else {
                filePath = path
            }
        }
        findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
    }

    private fun loadPhotoEditItems() {
        val list = mutableListOf<PhotoEditItem>()
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_adjust, getString(R.string.text_adjust)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_fillter, getString(R.string.text_filter)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_crop, getString(R.string.text_crop)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_frame, getString(R.string.text_frame)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_sticker, getString(R.string.text_sticker)))
        list.add(PhotoEditItem(R.mipmap.photoedit_icon_text, getString(R.string.text_text)))
        viewModel.setList(list)
    }

    /**
     * 横向图片旋转为纵向
     * @param srcPath
     */
    private fun rotatePic(srcPath: String) {

    }


    inner class PhotoEditClickProxy {

        fun back() {
            finish()
        }

        fun complete() {

        }

        fun cancel() {
            finish()
        }
    }

}