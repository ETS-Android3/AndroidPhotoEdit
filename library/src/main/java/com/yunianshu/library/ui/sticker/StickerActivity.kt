package com.yunianshu.library.ui.sticker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.Utils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.binding_recyclerview.adapter.BaseDataBindingAdapter
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.adapter.StickerAdapter
import com.yunianshu.library.bean.StickerInfo
import com.yunianshu.library.response.StickerResponse
import com.yunianshu.library.response.Stk
import com.yunianshu.library.util.HttpUtil
import com.yunianshu.library.util.Matrix3
import com.yunianshu.library.view.StickerView
import com.yunianshu.library.view.imagezoom.ImageViewTouch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import kotlin.concurrent.thread

/**
 * 5.贴纸界面
 */
class StickerActivity : BaseActivity(),BaseDataBindingAdapter.OnItemClickListener<Stk>{

    private lateinit var viewModel: StickerViewModel
    private lateinit var adapter: StickerAdapter
    private lateinit var bitmap: Bitmap
    private lateinit var url:String
    private val imageView: ImageViewTouch by lazy { findViewById(R.id.stickerImage) }
    private val stickerView: StickerView by lazy { findViewById(R.id.stickerView) }

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(StickerViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = StickerAdapter(this)
        adapter.setOnItemClickListener(this)
        return DataBindingConfig(R.layout.activity_sticker, BR.vm, viewModel)
            .addBindingParam(BR.adapter, adapter)
            .addBindingParam(BR.click, StickerClickProxy())
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        bitmap = BitmapFactory.decodeFile(url)
        viewModel.bitmap.postValue(bitmap)
        val layoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = "${bitmap.width}:${bitmap.height}"

//        loadStickers()
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


    inner class StickerClickProxy {

        fun back() {
            finish()
        }

        fun cancel() {
            finish()
        }

        fun complete() {
            // System.out.println("保存贴图!");
            val touchMatrix: Matrix = imageView.imageViewMatrix
            val resultBit: Bitmap = Bitmap.createBitmap(bitmap).copy(
                Bitmap.Config.ARGB_8888, true
            )
            val canvas = Canvas(resultBit)
            val data = FloatArray(9)
            touchMatrix.getValues(data) // 底部图片变化记录矩阵原始数据
            val cal = Matrix3(data) // 辅助矩阵计算类
            val inverseMatrix: Matrix3 = cal.inverseMatrix() // 计算逆矩阵
            val m = Matrix()
            m.setValues(inverseMatrix.values)

            val bank = stickerView.bank
            for(item in bank.values){
                item.matrix.postConcat(m) // 乘以底部图片变化矩阵
                canvas.drawBitmap(item.bitmap, item.matrix, null)
            }
            val path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "sticker_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            ImageUtils.save(resultBit,path,Bitmap.CompressFormat.JPEG)
            setResult(Contant.FILTER,intent.setData(Uri.fromFile(File(path))))
            finish()
        }
    }

    override fun onItemClick(viewId: Int, item: Stk, position: Int) {
        thread {
            var bitmap = Glide.with(this)
                .asBitmap()
                .load(item.image)
                .submit(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .get()
            runOnUiThread {
                stickerView.addBitImage(bitmap)
            }

        }

    }

}