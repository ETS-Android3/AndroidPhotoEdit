package com.yunianshu.photoedit

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.yunianshu.library.PhotoEditActivity
import com.yunianshu.library.ext.load
import com.yunianshu.photoedit.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.text.setOnClickListener {
            open()
        }
        binding.clear.setOnClickListener {
            var path = Utils.getApp()
                .getExternalFilesDir("edit")?.absolutePath
            path?.let {
                var delete = FileUtils.deleteAllInDir(it)
                if (delete) {
                   ToastUtils.showShort("清除成功")
                }
            }

        }
    }

    private fun open(){
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setMaxSelectNum(1)
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result != null) {
                        val last = result.last()
                        startActivity(last!!)
                    }
                }
                override fun onCancel() {

                }
            })
    }

    fun startActivity(localMedia: LocalMedia){
        val intent = Intent(this@MainActivity, PhotoEditActivity::class.java)
        intent.putExtra("url",localMedia.availablePath)
        intent.putExtra("typeName","CP4000")
        intent.putExtra("width",800)
        intent.putExtra("height",1200)
        if(localMedia.width>localMedia.height){
            intent.putExtra("rotate",true)
        }
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            val url = result.data?.getStringExtra("url")
           url?.let {
               binding.imageView2.load(it)
           }
        }.launch(intent)
    }
}