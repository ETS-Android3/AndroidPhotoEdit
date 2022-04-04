package com.yunianshu.library

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.text.Layout
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.UriUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.adapter.RgAdapter
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.ui.adjust.AdjustmentActivity
import com.yunianshu.library.ui.crop.CropActivity
import com.yunianshu.library.ui.fillter.FilterActivity
import com.yunianshu.library.ui.frame.FrameActivity
import com.yunianshu.library.ui.sticker.StickerActivity
import com.yunianshu.library.ui.words.TextFragment
import com.yunianshu.library.view.ModifyContentDialog
import com.yunianshu.library.view.StickerListener
import com.yunianshu.sticker.Sticker
import com.yunianshu.sticker.StickerView
import com.yunianshu.sticker.TextSticker
import java.io.File


/**
 *
 */
class PhotoEditActivity : BaseActivity() {

    private lateinit var viewModel: PhotoViewModel
    private lateinit var shareVM: ShareViewModel//fragment共享
    private lateinit var adapter: PhotoEditAdapter
    private lateinit var fragmentAdapter: RgAdapter
    private lateinit var dialog: ModifyContentDialog
    private lateinit var filePath: String
    private var rotate: Boolean = false
    private var width: Int = 0
    private var height: Int = 0
    private val stickerView: StickerView by lazy { findViewById(R.id.sticker_view) }

    /**
     * 1.先初始化ViewModel
     */
    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(PhotoViewModel::class.java)
        shareVM = getActivityScopeViewModel(ShareViewModel::class.java)
    }

    /**
     * 2.绑定视图
     */
    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = PhotoEditAdapter(this)
        fragmentAdapter = RgAdapter(this)
        return DataBindingConfig(R.layout.activity_photo_edit, BR.vm, viewModel)
            .addBindingParam(BR.click, PhotoEditClickProxy())
            .addBindingParam(BR.adapter, adapter)
            .addBindingParam(BR.fmadapter, fragmentAdapter)
            .addBindingParam(BR.sharevm, shareVM)
            .addBindingParam(BR.stickerClick, ShareStickerListener())
    }

    /**
     * 3.填充数据
     */
    override fun loadView() {
        immersionBar {
            statusBarColor(R.color.base_color)
        }
        dialog = ModifyContentDialog(this)
        initIntent()
        loadImage()
        loadPhotoEditItems()
        initLauncher()
        initFragment()
        stickerView.isConstrained = true
        shareVM.textStickerInfo.observeSticky(this) {
            if (stickerView.currentSticker == null) {
                showInputDialog(it)
            } else {
                updateSticker(it)
            }
        }
        shareVM.textStickerAlpha.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setAlpha(it.toInt())
                viewModel.refreshStickerView()
            }
        }
        shareVM.textStickerAlign.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                when (it) {
                    0 -> sticker.setTextAlign(Layout.Alignment.ALIGN_NORMAL)
                    1 -> sticker.setTextAlign(Layout.Alignment.ALIGN_OPPOSITE)
                    2 -> sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER)
                }
                sticker.resizeText()
                viewModel.refreshStickerView()
            }
        }
        shareVM.textStickerBold.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                if (shareVM.textStickerItalic.value == true) {
                    if (it) {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.BOLD_ITALIC
                            )
                        )
                    } else {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.ITALIC
                            )
                        )
                    }
                } else {
                    if (it) {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.BOLD
                            )
                        )
                    } else {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.NORMAL
                            )
                        )
                    }
                }
                sticker.setFakeBoldText(it)
                viewModel.refreshStickerView()
            }
        }
        shareVM.textStickerColor.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setTextColor(it.color)
                viewModel.refreshStickerView()
            }
        }
        shareVM.textStickerItalic.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                if (shareVM.textStickerBold.value == true) {
                    if (it) {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.BOLD_ITALIC
                            )
                        )
                    } else {
                        sticker.setTypeface(
                            Typeface.create(
                                Typeface.SANS_SERIF,
                                Typeface.BOLD
                            )
                        )
                    }
                } else {
                    if (it) {
                        sticker.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC))
                    } else {
                        sticker.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
                    }
                }
                viewModel.refreshStickerView()
            }
        }
        shareVM.textStickerUnderline.observeSticky(this) {
            if (stickerView.currentSticker != null) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setTextUnderLine(it)
                viewModel.refreshStickerView()
            }
        }
    }

    /**
     * 获取传入参数
     */
    private fun initIntent() {
        filePath = intent.getStringExtra("url").toString()
        rotate = intent.getBooleanExtra("rotate", false)
        width = intent.getIntExtra("width", 0)
        height = intent.getIntExtra("height", 0)
    }

    /**
     * 设置跳转回调
     */
    private fun initLauncher() {
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
        adapter.setOnItemClickListener { _, item, position ->
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
                    shareVM.showEditView.postValue(true)
                    viewModel.currentPaper.postValue(0)
                    viewModel.currentText.postValue(item.text)
                    immersionBar {
                        statusBarColor(com.yunianshu.sticker.R.color.white)
                        statusBarDarkFont(true)
                    }
                    return@setOnItemClickListener
                }
            }
            intent?.putExtra("url", filePath)
            activityResultLauncher.launch(intent)
        }
    }

    private fun initFragment() {
        fragmentAdapter.addFragment(TextFragment())
    }

    private fun tempPath(): String {
        return Utils.getApp().getExternalFilesDir("tmp")!!.absolutePath
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
            val path =
                tempPath() + File.separator + "rotate_" + com.yunianshu.library.util.ImageUtils.hashCode(
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

    inner class PhotoEditClickProxy {

        fun last() {
            shareVM.showEditView.postValue(false)
            immersionBar {
                statusBarColor(R.color.base_color)
                statusBarDarkFont(false)
            }
        }

        fun back() {
            finish()
        }

        fun complete() {

        }

        fun save() {

        }
    }

    inner class ShareStickerListener : StickerListener() {

        override fun onStickerClicked(sticker: Sticker) {
            when (sticker) {
                is TextSticker -> {
                    showInputDialog(sticker)
                }
            }
        }
    }

    /**
     * 更新文字气泡背景
     */
    private fun updateSticker(item: StickerInfo) {
        val sticker = stickerView.currentSticker as TextSticker
        val bubbleInfo = item.bubbleInfo
        when (bubbleInfo!!.type) {
            Contant.STICKER_TYPE_TEXT -> {
                sticker.reset()
                showInputDialog(sticker)
            }
            Contant.STICKER_TYPE_TEXT_BUBBLE -> {
                val drawable = BitmapDrawable(
                    resources,
                    assets.open(item.bubbleInfo!!.path)
                )
                sticker.setDrawable(
                    drawable,
                    Rect(
                        ConvertUtils.px2dp(bubbleInfo.paddingLeft.toFloat()),
                        ConvertUtils.px2dp(bubbleInfo.paddingTop.toFloat()),
                        drawable.intrinsicWidth - ConvertUtils.px2dp(bubbleInfo.paddingRight.toFloat()),
                        drawable.intrinsicHeight - ConvertUtils.px2dp(bubbleInfo.paddingBottom.toFloat())
                    )
                )
            }
        }
        sticker.resizeText()
        viewModel.refreshStickerView()
    }

    private fun showInputDialog(item: StickerInfo) {
        dialog.setHint(R.string.tip_enter_content)
            .setOnTextChangedListener(false) {
                if(it.isEmpty()){
                    return@setOnTextChangedListener
                }
                val sticker = TextSticker(this)
                val bubbleInfo = item.bubbleInfo
                sticker.setText(it.toString())
                    .setMaxTextSize(24f)
                    .setAlpha(255)
                when (item.bubbleInfo!!.type) {
                    Contant.STICKER_TYPE_TEXT -> {

                    }
                    Contant.STICKER_TYPE_TEXT_BUBBLE -> {
                        //获取assets图片
                        val drawable = BitmapDrawable(
                            resources,
                            assets.open(item.bubbleInfo!!.path)
                        )
                        //设置文字的有效范围
                        sticker.setDrawable(
                            drawable,
                            Rect(
                                ConvertUtils.px2dp(bubbleInfo!!.paddingLeft.toFloat()),
                                ConvertUtils.px2dp(bubbleInfo.paddingTop.toFloat()),
                                drawable.intrinsicWidth - ConvertUtils.px2dp(bubbleInfo.paddingRight.toFloat()),
                                drawable.intrinsicHeight - ConvertUtils.px2dp(bubbleInfo.paddingBottom.toFloat())
                            )
                        )
                    }
                }
                sticker.resizeText()
                shareVM.addSticker(sticker)
            }.show()
    }

    fun showInputDialog(sticker: TextSticker) {
        dialog.setHint(R.string.tip_enter_content)
            .setContent(sticker.text)
            .setOnTextChangedListener(false) {
                if (!TextUtils.isEmpty(it.toString())) {
                    sticker.text = it.toString()
                    sticker.resizeText()
                    viewModel.refreshStickerView()
                }
            }.show()
    }


}