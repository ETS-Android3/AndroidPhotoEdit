package com.yunianshu.library.ui.adjust

import android.graphics.*
import android.net.Uri
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.indicatorseekbar.widget.SeekParams
import com.yunianshu.library.*
import com.yunianshu.library.Contant.KEY_ROTATE
import com.yunianshu.library.Contant.KEY_URL
import com.yunianshu.library.bean.AdjustBaseInfo
import com.yunianshu.library.bean.PhotoEditItem
import com.yunianshu.library.util.GPUImageFilterTools
import jp.co.cyberagent.android.gpuimage.GLTextureView
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import java.io.File
import kotlin.concurrent.thread

/**
 * 1.调整处理界面
 */
class AdjustmentActivity : BaseActivity() {

    private var infos = mutableListOf<AdjustBaseInfo>()

    init {
        //初始化调节参数
        infos.add(AdjustBaseInfo(0, 0f, 100f, 50f))//亮度
        infos.add(AdjustBaseInfo(1, 0f, 100f, 25f))//对比度
        infos.add(AdjustBaseInfo(2, 0f, 100f, 50f))//饱和度
        infos.add(AdjustBaseInfo(3, 0f, 100f, 50f))//锐化
        infos.add(AdjustBaseInfo(4, 0f, 100f, 50f))//曝光
        infos.add(AdjustBaseInfo(5, 0f, 100f))//阴影
        infos.add(AdjustBaseInfo(6, 0f, 100f, 50f))//色温
        infos.add(AdjustBaseInfo(7, 0f, 100f))//暗角
        initGPUImageFilterGroup()
    }

    private lateinit var viewModel: AdjustViewModel
    private lateinit var url: String
    private var rotate: Boolean = false
    private lateinit var adapter: PhotoEditAdapter
    private var mBmpW: Int = 0
    private var mBmpH: Int = 0

    private val surfaceView: GLTextureView by lazy { findViewById(R.id.imageView) }
    private lateinit var gpuImage: GPUImage
    private var filterAdjuster: GPUImageFilterTools.FilterAdjuster? = null
    private lateinit var group: GPUImageFilterGroup


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(AdjustViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = PhotoEditAdapter(this)
        return DataBindingConfig(
            R.layout.activity_adjustment,
            BR.vm,
            viewModel
        ).addBindingParam(BR.click, AdjustClickProxy())
            .addBindingParam(BR.listener, SeekBarListener())
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(KEY_URL).toString()
        rotate = intent.getBooleanExtra(KEY_ROTATE, false)
        var bitmap = BitmapFactory.decodeFile(url)
        mBmpW = bitmap.width
        mBmpH = bitmap.height
        val layoutParams = surfaceView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = "$mBmpW:$mBmpH"
        gpuImage = GPUImage(this)
        gpuImage.setBackgroundColor(255f, 255f, 255f)
        gpuImage.setGLTextureView(surfaceView)
        thread {
            gpuImage.setImage(
                if (rotate)
                    ImageUtils.rotate(bitmap, 90, bitmap.width.toFloat(), bitmap.height.toFloat())
                else
                    bitmap
            )
        }
        loadPhotoEditItems()
        viewModel.url.postValue(url)
        viewModel.setInfo(infos)
        viewModel.curBaseInfo.postValue(infos[0])
        viewModel.selectAdjust(getString(R.string.text_brightness))
        switchFilterTo(group.filters[0])
        adapter.setOnItemClickListener { _, item, position ->
            viewModel.selectAdjust(item.text)
            viewModel.select(position)
            switchFilterTo(group.filters[position])
        }
    }

    private fun initGPUImageFilterGroup() {
        group = GPUImageFilterGroup(
            listOf(
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.BRIGHTNESS
                ),/*亮度*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.CONTRAST
                ),/*对比度*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.SATURATION
                ),/*饱和度*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.SHARPEN
                ),/*锐化*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.EXPOSURE
                ),/*曝光*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.HIGHLIGHT_SHADOW
                ),/*阴影*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.WHITE_BALANCE
                ),/*色温*/
                GPUImageFilterTools.createFilterForType(
                    this,
                    GPUImageFilterTools.FilterType.VIGNETTE
                )/*暗角*/
            )
        )
    }

    private fun loadPhotoEditItems() {
        val list = mutableListOf<PhotoEditItem>()
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_brightness,
                getString(R.string.text_brightness)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_contrast,
                getString(R.string.text_contrast)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_ratio,
                getString(R.string.text_ratio)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_sharp,
                getString(R.string.text_sharp)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_exposure,
                getString(R.string.text_exposure)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_shadown,
                getString(R.string.text_shadown)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_color_temperature,
                getString(R.string.text_color_temperature)
            )
        )
        list.add(
            PhotoEditItem(
                R.mipmap.photoedit_icon_dark_angle,
                getString(R.string.text_dark_angle)
            )
        )
        viewModel.setList(list)

    }

    inner class AdjustClickProxy {

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {
            val bitmap = gpuImage.bitmapWithFilterApplied
            var path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "adjust_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            ImageUtils.save(bitmap,path,Bitmap.CompressFormat.JPEG)
            setResult(Contant.ADJUST, intent.setData(Uri.fromFile(File(path))))
            finish()
        }
    }

    inner class SeekBarListener : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams) {
            filterAdjuster?.adjust(seekParams.progress)
            gpuImage.setFilter(group)
            gpuImage.requestRender()
        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
            //记录调节类型的位置
            viewModel.setProgressChanged(seekBar.progress)
        }

    }

    private fun switchFilterTo(filter: GPUImageFilter) {
        filterAdjuster = GPUImageFilterTools.FilterAdjuster(filter)
    }

}