package com.yunianshu.library

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.Layout
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.blankj.utilcode.util.*
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.adapter.RgAdapter
import com.yunianshu.library.bean.BubbleInfo
import com.yunianshu.library.bean.FontInfo
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.ui.adjust.AdjustmentActivity
import com.yunianshu.library.ui.crop.CropActivity
import com.yunianshu.library.ui.fillter.FilterActivity
import com.yunianshu.library.ui.frame.FrameActivity
import com.yunianshu.library.ui.sticker.StickerFragment
import com.yunianshu.library.ui.text.TextFragment
import com.yunianshu.library.util.HttpUtil.fetchDownload
import com.yunianshu.library.view.ModifyTextContentDialog
import com.yunianshu.library.view.StickerListener
import com.yunianshu.sticker.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


/**
 *
 */
class PhotoEditActivity : BaseActivity() {

    private lateinit var viewModel: PhotoViewModel

    //fragment共享
    private lateinit var shareVM: ShareViewModel
    private lateinit var adapter: PhotoEditAdapter
    private lateinit var fragmentAdapter: RgAdapter
    private lateinit var dialog: ModifyTextContentDialog
    private lateinit var filePath: String
    private lateinit var fragmentList: MutableList<Fragment>

    //图片位置
    private var currentIndex: Int = 0
    private var rotate: Boolean = false
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var typeName: String
    private val stickerView: StickerView by lazy { findViewById(R.id.sticker_view) }
    private val imageUndo: TextView by lazy { findViewById(R.id.edit_image_undo) }
    private val imageRedo: TextView by lazy { findViewById(R.id.edit_image_redo) }
    private val title: TextView by lazy { findViewById(R.id.title) }

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
        initIntent()
        loadImage()
        loadPhotoEditItems()
        initLauncher()
        initFragment()
        initStickerView()
        addListener()
        addObserve()
    }

    private fun initStickerView() {
        dialog = ModifyTextContentDialog(this)
        stickerView.isConstrained = true
        imageRedo.visibility = View.GONE
        imageUndo.visibility = View.GONE
        title.text = getString(R.string.text_print_preview)
    }

    /**
     * 添加监听
     */
    private fun addListener() {
        imageRedo.setOnClickListener {
            val values = shareVM.cacheImagePaths.value
            values?.let {
                if (currentIndex + 1 < values.size) {
                    currentIndex++
                    filePath = values[currentIndex]
                    if (currentIndex + 1 == values.size) {
                        imageRedo.visibility = View.GONE
                    }
                    imageUndo.visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(
                        BitmapFactory.decodeFile(
                            filePath
                        )
                    )
                }
            }
        }
        imageUndo.setOnClickListener {
            val values = shareVM.cacheImagePaths.value
            values?.let {
                if (currentIndex - 1 >= 0) {
                    currentIndex--
                    filePath = values[currentIndex]
                    var bitmap = BitmapFactory.decodeFile(filePath)
                    if (currentIndex == 0) {
                        imageUndo.visibility = View.GONE
                        if (rotate) {
                            bitmap = ImageUtils.rotate(bitmap, 90, 0f, 0f)
                        }
                    }
                    imageRedo.visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.imageView).setImageBitmap(bitmap)
                }
            }
        }
    }

    /**
     * 添加viewModel的观察者
     */
    private fun addObserve() {
        shareVM.showTextEditView.observeSticky(this) {
            if (it) {
                imageRedo.visibility = View.GONE
                imageUndo.visibility = View.GONE
            }
        }
        shareVM.textStickerInfo.observeSticky(this) {
//            if (stickerView.currentSticker == null) {
//                addTextSticker(it)
//            } else {
//                updateSticker(it)
//            }
            if(shareVM.showTextEditView.value == true){
                addTextSticker(it)
            }
        }
        //设置文字透明度
        shareVM.textStickerAlpha.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setAlpha(it.toInt())
                viewModel.refreshStickerView()
            }
        }
        //设置排列方式
        shareVM.textStickerAlign.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
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
        //设置文字粗细
        shareVM.textStickerBold.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
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
        //文字颜色
        shareVM.textStickerColor.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setTextColor(it.color)
                viewModel.refreshStickerView()
            }
        }
        //设置字体斜体
        shareVM.textStickerItalic.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
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
        //设置下划线
        shareVM.textStickerUnderline.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
                val sticker = stickerView.currentSticker as TextSticker
                sticker.setTextUnderLine(it)
                viewModel.refreshStickerView()
            }
        }

        //设置文字阴影
        shareVM.textStickerShadow.observeSticky(this) {
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
                val sticker = stickerView.currentSticker as TextSticker
                if(it){
                    sticker.setShadowLayer(Color.BLUE)
                }else{
                    sticker.setShadowLayer(Color.TRANSPARENT)
                }
                viewModel.refreshStickerView()
            }
        }


        //设置字体
        shareVM.textStickerFont.observeSticky(this) { info ->
            if (stickerView.currentSticker != null && stickerView.currentSticker is TextSticker) {
                val sticker = stickerView.currentSticker as TextSticker
                when (info.type) {
                    0 -> {//默认字体
                        sticker.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
                    }
                    1 -> {//本地字体
                        info.filePath?.let { path ->
                            val file = File(path)
                            if (file.exists()) {
                                sticker.setTypeface(Typeface.createFromFile(file))
                            } else {
                                ToastUtils.showShort("本地找不到该下载字体")
                            }
                        }
                    }
                    2 -> {//网路字体
                        GlobalScope.launch {
                            try {
                                downLoadFont(info)
                            } catch (e: Exception) {
                                ToastUtils.showShort("下载字体失败,请重试")
                            }
                        }

                    }
                }
                sticker.resizeText()
                viewModel.refreshStickerView()
            }
        }
    }


    /**
     * 下载字体设置
     */
    private suspend fun downLoadFont(info: FontInfo) {
        val sticker = stickerView.currentSticker as TextSticker
        info.url?.let { url ->
            val path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + url.substring(
                url.lastIndexOf(
                    "."
                ) - 8
            )
            File(path).let {
                if (!it.exists()) {
                    val response = fetchDownload(this, url, path)
                    if (response == path) {
                        sticker.setTypeface(Typeface.createFromFile(it))
                        info.type = 1
                        info.filePath = path
                    } else {
                        ToastUtils.showShort("下载失败")
                    }
                } else {
                    //下载的字体已存在
                    info.type = 1
                    info.filePath = path
                    sticker.setTypeface(Typeface.createFromFile(File(path)))
                }
                sticker.resizeText()
                viewModel.refreshStickerView()
            }

        }
    }

    /**
     * 添加文字气泡
     */
    private fun addTextSticker(info: StickerInfo) {
        var sticker: Sticker? = null
        val bubbleInfo = info.bubbleInfo
        when (bubbleInfo!!.type) {
            Contant.STICKER_TYPE_DEFAULT -> {
                sticker = DrawableSticker(BitmapDrawable(resources, info.bitmap))
            }
            Contant.STICKER_TYPE_TEXT -> {
                sticker = TextSticker(applicationContext)
                sticker.setText("请输入文字")
                    .setMaxTextSize(20f)
                    .setTextSize(16f)
                    .setAlpha(255)
                sticker.resizeText()
            }
            Contant.STICKER_TYPE_TEXT_BUBBLE -> {
                sticker = TextSticker(applicationContext)
                sticker.setText("请输入文字")
                    .setMaxTextSize(20f)
                    .setTextSize(16f)
                    .setAlpha(255)
                //获取assets图片
                val drawable = BitmapDrawable(
                    resources,
                    assets.open(info.bubbleInfo!!.path)
                )
                //设置文字的有效范围
                sticker.setDrawable(
                    drawable,
                    Rect(
                        ConvertUtils.px2dp(bubbleInfo.paddingLeft.toFloat()),
                        ConvertUtils.px2dp(bubbleInfo.paddingTop.toFloat()),
                        drawable.intrinsicWidth - ConvertUtils.px2dp(bubbleInfo.paddingRight.toFloat()),
                        drawable.intrinsicHeight - ConvertUtils.px2dp(bubbleInfo.paddingBottom.toFloat())
                    )
                )
                sticker.resizeText()
            }
        }
        shareVM.addSticker(sticker!!)
        viewModel.refreshStickerView()
    }

    /**
     * 获取传入参数
     */
    private fun initIntent() {
        filePath = intent.getStringExtra("url").toString()
        rotate = intent.getBooleanExtra("rotate", false)
        width = intent.getIntExtra("width", 0)
        height = intent.getIntExtra("height", 0)
        typeName = intent.getStringExtra("typeName").toString()
        shareVM.cacheImagePaths.postValue(mutableListOf(filePath))
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
                    var values = shareVM.cacheImagePaths.value
                    (values as MutableList<String>).add(filePath)
                    currentIndex = values.size - 1
                    imageUndo.visibility = View.VISIBLE
                    imageRedo.visibility = View.GONE
                    title.text = ""
                }
            }
        adapter.setOnItemClickListener { _, item, position ->
            //设置编辑转态
            shareVM.editState.postValue(position)
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
                    viewModel.currentText.postValue(item.text)
                    onStickerClick()
                    return@setOnItemClickListener
                }
                Contant.WORDS -> {
                    startText(item.text)
                    return@setOnItemClickListener
                }
            }
            intent?.putExtra("url", filePath)
            intent?.putExtra("typeName", typeName)
            activityResultLauncher.launch(intent)
        }
    }

    /**
     * 点击 5-贴图
     */
    private fun onStickerClick() {
        stickerView.isLocked = false
        stickerView.show()
        shareVM.showTextEditView.postValue(true)
        viewModel.currentPaper.postValue(1)
        immersionBar {
            statusBarColor(com.yunianshu.sticker.R.color.white)
            statusBarDarkFont(true)
        }
    }

    /**
     * 点击 6-文字
     */
    private fun startText(name: String) {
        stickerView.isLocked = false
        shareVM.textStickerInfo.postValue(
            StickerInfo(
                bitmap = com.yunianshu.library.util.ImageUtils.drawableToBitmap(
                    TextDrawable.builder()
                        .beginConfig()
                        .width(100)
                        .height(50)
                        .endConfig()
                        .buildRoundRect("Hi", Color.parseColor("#82D0E7"), 5)
                ),
                bubbleInfo = BubbleInfo(
                    type = Contant.STICKER_TYPE_TEXT,
                    paddingLeft = 10,
                    paddingBottom = 10,
                    paddingRight = 10,
                    paddingTop = 10
                ),
                select = true
            )
        )
        stickerView.show()
        shareVM.showTextEditView.postValue(true)
        viewModel.currentPaper.postValue(0)
        viewModel.currentText.postValue(name)
        immersionBar {
            statusBarColor(com.yunianshu.sticker.R.color.white)
            statusBarDarkFont(true)
        }
    }

    private fun initFragment() {
        val textFragment = TextFragment()
        val stickerFragment = StickerFragment()
        fragmentAdapter.addFragment(textFragment)
        fragmentAdapter.addFragment(stickerFragment)
        findViewById<ViewPager2>(R.id.viewPager2).offscreenPageLimit = 2
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
        //取消编辑
        fun cancel() {
            shareVM.showTextEditView.postValue(false)
            dialog.dismiss()
            immersionBar {
                statusBarColor(R.color.base_color)
                statusBarDarkFont(false)
            }
            stickerView.isLocked = true
        }

        fun back() {
            finish()
        }

        fun complete() {
            shareVM.showTextEditView.postValue(false)
            dialog.dismiss()
            stickerView.hide()
            immersionBar {
                statusBarColor(R.color.base_color)
                statusBarDarkFont(false)
            }
            stickerView.isLocked = true
        }

        fun save() {
            var path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "result_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            stickerView.save(File(path))
            setResult(Activity.RESULT_OK, Intent().putExtra("url", path))
            finish()
        }
    }

    inner class ShareStickerListener : StickerListener() {

        override fun onStickerClicked(sticker: Sticker) {
            when (sticker) {
                is TextSticker -> {
                    dialog.isShowing.let {
                        if (it) {
                            dialog.dismiss()
                            stickerView.hide()
                        } else {
                            showInputDialog(sticker)
                            stickerView.show()
                        }
                    }
                }
                is DrawableSticker -> {
                    if (stickerView.isShow) {
                        stickerView.hide()
                    } else {
                        stickerView.show()
                    }
                }
            }
        }

        override fun onStickerDeleted(sticker: Sticker) {
            shareVM.removeSticker(sticker)
            dialog.isShowing.let {
                if (it) {
                    dialog.dismiss()
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

    /**
     * 添加文字气泡
     */
    private fun showInputDialog(item: StickerInfo) {
        dialog.setHint(R.string.tip_enter_content)
            .setOnTextChangedListener(false,
                object : ModifyTextContentDialog.OnTextChangedListener {

                    override fun onTextChange(charSequence: CharSequence) {
                        if (charSequence.isEmpty()) {
                            return
                        }
                        val sticker = TextSticker(this@PhotoEditActivity)
                        val bubbleInfo = item.bubbleInfo
                        sticker.setText(charSequence.toString())
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
                    }
                }).show()
    }

    /**
     * 单纯添加文字
     */
    fun showInputDialog(sticker: TextSticker) {
        dialog.setHint(R.string.tip_enter_content)
            .setContent(sticker.text!!)
            .setOnTextChangedListener(false,
                object : ModifyTextContentDialog.OnTextChangedListener {

                    override fun onTextChange(it: CharSequence) {
                        if (!TextUtils.isEmpty(it.toString())) {
                            sticker.text = it.toString()
                            sticker.drawable = TextDrawable.builder().beginConfig().width(200).height(50)
                                .endConfig()
                                .buildRoundRect("", Color.argb(255, 131, 131, 131), 5)
                            sticker.resizeText()
                            viewModel.refreshStickerView()
                        }
                    }
                }
            ).show()
    }


}