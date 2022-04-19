package com.yunianshu.library.ui.fillter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.lxj.xpopup.XPopup
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing
import com.yunianshu.library.*
import com.yunianshu.library.adapter.FilterAdapter
import com.yunianshu.library.bean.FilterItem
import com.yunianshu.library.util.RecycleViewUtils.toPosition
import java.io.File
import kotlin.concurrent.thread

/**
 * 2.滤镜处理界面
 */
class FilterActivity : BaseActivity() {
    private lateinit var viewModel: FilterViewModel
    private lateinit var adapter: FilterAdapter
    private val imageView:ImageView by lazy { findViewById(R.id.filterImageView) }


    override fun initViewModel() {
        viewModel = getActivityScopeViewModel(FilterViewModel::class.java)
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        adapter = FilterAdapter(this)
       return DataBindingConfig(R.layout.activity_filter,BR.vm,viewModel)
           .addBindingParam(BR.click,FilterClickProxy())
           .addBindingParam(BR.adapter,adapter)
           .addBindingParam(BR.anim,DefaultItemAnimator())

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun loadView() {
        immersionBar {
            statusBarDarkFont(true)
        }
        adapter.setOnItemClickListener { _, item, pos ->
            if(!item.select){
                viewModel.currentItem.value!!.select = false
                item.select = true
                viewModel.currentItem.postValue(item)
                adapter.notifyDataSetChanged()
                toPosition(findViewById(R.id.recyclerView2),pos,13,2)
            }
        }
        val url = intent.getStringExtra(Contant.KEY_URL)
        val filterList = mutableListOf<FilterItem>()
        val bitmap = BitmapFactory.decodeFile(url)
        val file = File(url)
        if(!file.exists()){
            ToastUtils.showShort("file not exists")
        }
        bitmap?.let {
            val beginTime = System.currentTimeMillis()
            val layoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.dimensionRatio = "${bitmap.width}:${bitmap.height}"
            val arrFilters = resources.getStringArray(R.array.filters)
            val loading = XPopup.Builder(this@FilterActivity)
                .dismissOnBackPressed(false)
                .isDarkTheme(false)
                .isLightNavigationBar(true)
                .isViewMode(true)
                .asLoading()
            loading.show()
            thread {
                for (i in 0..12){
                    filterList.add(FilterItem(bitmap = PhotoProcessing.filterPhoto(
                        Bitmap.createBitmap(
                            bitmap.copy(
                                Bitmap.Config.ARGB_8888, true
                            )
                        ),i), text = arrFilters[i]))
                }
                runOnUiThread {
                    loading.dismiss()
                }
                viewModel.currentItem.postValue(filterList[0])
                val endTime = System.currentTimeMillis()
                Log.e(localClassName,"beginTime:$beginTime---endTime:$endTime---${endTime-beginTime}")
                viewModel.list.postValue(filterList)
            }
        }
    }

    inner class FilterClickProxy{

        fun back(){
            finish()
        }

        fun cancel(){
            finish()
        }

        fun complete(){
            var path = Utils.getApp()
                .getExternalFilesDir("edit")!!.absolutePath + File.separator + "filter_" + System.currentTimeMillis() + ".jpg"
            FileUtils.createOrExistsFile(path)
            ImageUtils.save(viewModel.currentItem.value!!.bitmap,path,Bitmap.CompressFormat.JPEG)
            setResult(Contant.FILTER,intent.setData(Uri.fromFile(File(path))))
            finish()
        }
    }
}