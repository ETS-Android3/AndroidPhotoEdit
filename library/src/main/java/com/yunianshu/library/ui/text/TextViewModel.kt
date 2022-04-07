package com.yunianshu.library.ui.text

import androidx.lifecycle.ViewModel
import com.kunminx.architecture.ui.callback.UnPeekLiveData

class TextViewModel : ViewModel() {

   var editType = UnPeekLiveData<Int>()

}