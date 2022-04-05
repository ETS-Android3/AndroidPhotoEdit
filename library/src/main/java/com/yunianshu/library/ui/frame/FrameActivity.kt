package com.yunianshu.library.ui.frame

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.yunianshu.library.BR
import com.yunianshu.library.BaseActivity
import com.yunianshu.library.Contant
import com.yunianshu.library.R
import com.yunianshu.library.adapter.FrameAdapter
import com.yunianshu.library.bean.FrameInfo
import com.yunianshu.library.databinding.ActivityFrameBinding
import com.yunianshu.library.ext.okhttp.http
import com.yunianshu.library.ext.okhttp.post
import com.yunianshu.library.response.Frame
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 4.相框调整
 */
class FrameActivity : BaseActivity() {

    private lateinit var viewModel: FrameViewModel
    private lateinit var adapter: FrameAdapter
    private lateinit var url: String
    private val binding: ActivityFrameBinding by lazy { ActivityFrameBinding.inflate(layoutInflater) }
    var lastx //手指在屏幕上抬起时的位置x
            = 0
    var lasty //手指在屏幕上抬起时的位置y
            = 0

    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FrameViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FrameAdapter(this)
        return DataBindingConfig(R.layout.activity_frame, BR.vm, viewModel)
            .addBindingParam(BR.click, FrameClickProxy())
            .addBindingParam(BR.adapter, adapter)
    }

    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        url = intent.getStringExtra(Contant.KEY_URL).toString()
        viewModel.url.postValue(url)
        //为图片添加移动监听
        binding.imageView.setOnTouchListener(object : View.OnTouchListener {
            var startx= 0  //手指第一次点击屏幕时的位置x
            var starty = 0 //手指第一次点击屏幕时的位置y

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startx = event.rawX.toInt()
                        starty = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_UP -> {
                        lastx = binding.imageView.getLeft()
                        println("lastx:$lastx")
                        lasty = binding.imageView.getTop()
                        println("lasty:$lasty")
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val x = event.rawX.toInt()
                        val y = event.rawY.toInt()
                        val dx = x - startx
                        val dy = y - starty
                        binding.imageView.layout(
                            binding.imageView.getLeft() + dx,
                            binding.imageView.getTop() + dy,
                            binding.imageView.getRight() + dx,
                            binding.imageView.getBottom() + dy
                        )
                        startx = event.rawX.toInt()
                        starty = event.rawY.toInt()
                        binding.imageView.invalidate()
                    }
                }
                return true
            }
        })


        var frames = mutableListOf<Frame>()
        frames.add(Frame(src = R.drawable.frame1, thumbnail = ""))
        frames.add(Frame(src = R.drawable.frame2, thumbnail = ""))
        viewModel.list.postValue(frames)

        adapter.setOnItemClickListener { _, item, _ ->
            viewModel.bitmap.postValue(item.src)
        }
    }

    /**
     * 获取网络资源
     */

    fun getHttpData() {
        GlobalScope.launch {
            var frameInfo =
                "https://businessapi.hprtupgrade.com/api/bphoto.beautiful_photos/dataPhotoFrameList".http()
                    .params(mapOf("page" to 0, "size" to 50, "model" to "Cp4000"))
                    .post<FrameInfo>().await()

        }
    }

    //获取合并图片
    fun montage(xiangkuang: Bitmap, phto: Bitmap?, phtoX: Int, phtoY: Int): Bitmap? {
        val newBitmap =
            Bitmap.createBitmap(xiangkuang.width, xiangkuang.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(phto!!, phtoX.toFloat(), phtoY.toFloat(), null)
        canvas.drawBitmap(xiangkuang, 0f, 0f, null)
        return newBitmap
    }

    inner class FrameClickProxy {

        fun cancel() {
            finish()
        }

        /**
         * 保存图片
         */
        fun complete() {

        }

    }

}