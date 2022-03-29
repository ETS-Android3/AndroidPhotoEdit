package com.yunianshu.library.ui.fillter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.xinlan.imageeditlibrary.editimage.fliter.PhotoProcessing
import com.yunianshu.library.*
import com.yunianshu.library.adapter.FilterAdapter
import com.yunianshu.library.bean.FilterItem

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

    }

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
                toPosition(pos,13)
            }
        }
        var url = intent.getStringExtra(Contant.KEY_URL)
        var filterList = mutableListOf<FilterItem>()
        val bitmap = BitmapFactory.decodeFile(url)
        val beginTime = System.currentTimeMillis()
        val layoutParams = imageView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.dimensionRatio = "${bitmap.width}:${bitmap.height}"
        val arrFilters = resources.getStringArray(R.array.filters)
        for (i in 0..12){
            filterList.add(FilterItem(bitmap = PhotoProcessing.filterPhoto(
                Bitmap.createBitmap(
                    bitmap.copy(
                        Bitmap.Config.ARGB_8888, true
                    )
                ),i), text = arrFilters[i]))
        }
        viewModel.currentItem.postValue(filterList[0])
        val endTime = System.currentTimeMillis()
        Log.e(localClassName,"beginTime:$beginTime---endTime:$endTime---${endTime-beginTime}")
        viewModel.list.postValue(filterList)
    }

    var mFirstVisiblePosition:Int = 0; //上次点击的位置
    private fun toPosition(i:Int, size:Int) { // i 当前点击的条目，size数据总长度
        var ScrollToPosition = mFirstVisiblePosition; //要滑动的位置
        if (i - mFirstVisiblePosition > 0) { //右边
            if (i + 2 < size) { //保证在数据长度内
                ScrollToPosition = i + 2;
            } else {
                ScrollToPosition = size;
            }
        } else { //左边
            if (i - 2 > 0) { //保证不会越界
                ScrollToPosition = i - 2;
            } else {
                ScrollToPosition = 0;
            }
        }
        findViewById<RecyclerView>(R.id.recyclerView2).smoothScrollToPosition(ScrollToPosition); //滑动方法
        mFirstVisiblePosition = i; //重新赋值
    }

    inner class FilterClickProxy{

        fun back(){
            finish()
        }

        fun cancel(){
            finish()
        }

        fun complete(){

        }
    }
}