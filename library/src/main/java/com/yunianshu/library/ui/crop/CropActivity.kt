package com.yunianshu.library.ui.crop

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.theartofdev.edmodo.cropper.CropImageView
import com.yunianshu.indicatorseekbar.widget.IndicatorSeekBar
import com.yunianshu.indicatorseekbar.widget.OnSeekChangeListener
import com.yunianshu.indicatorseekbar.widget.SeekParams
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.bean.CropViewBase
import com.yunianshu.library.ui.adjust.AdjustmentActivity
import java.io.File

/**
 * 3.裁剪界面
 */
class CropActivity : BaseActivity() {

    private lateinit var viewModel: CropViewModel
    private var width: Int = 0
    private var height: Int = 0
    private var rotate: Boolean = false
    private lateinit var url: String

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(CropViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.activity_crop, BR.vm, viewModel)
            .addBindingParam(BR.click, CropClickProxy())
            .addBindingParam(BR.listener, CropSeekBarListener())
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        width = intent.getIntExtra(Contant.KEY_WIDTH, -1)
        height = intent.getIntExtra(Contant.KEY_HEIGHT, -1)
        rotate = intent.getBooleanExtra(Contant.KEY_ROTATE, false)
        val bitmap = BitmapFactory.decodeFile(url)
        if (bitmap == null) {
            Toast.makeText(this, getString(R.string.text_image_no_find), Toast.LENGTH_LONG).show()
            return
        }
        initCropView()
    }

    private fun initCropView() {
        viewModel.cropViewBase.postValue(CropViewBase(width, height, Uri.parse(url)))
    }

    inner class CropClickProxy {

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {
            var path = Utils.getApp()
                .getExternalFilesDir("tmp")!!.absolutePath + File.separator + "crop_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            findViewById<CropImageView>(R.id.cropImageView).saveCroppedImageAsync(
                Uri.fromFile(
                    File(
                        path
                    )
                )
            )
            setResult(Contant.CROP, intent.setData(Uri.fromFile(File(path))))
        }
        /**
         * 镜像
         */
        fun image(){

        }

        fun rotate90(){

        }
    }

    inner class CropSeekBarListener : OnSeekChangeListener {

        override fun onSeeking(seekParams: SeekParams) {

        }

        override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
        }

        override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {

        }

    }
}