package com.yunianshu.library.view;

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.KeyboardUtils
import com.yunianshu.library.R

class ClearEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = androidx.appcompat.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {
    private val drawableClear: Drawable?
    private val drawableLabel: Drawable?
    private var listener:OnFinishComposingListener? = null

    init {
        val arr: TypedArray =
            context.obtainStyledAttributes(attrs, R.styleable.ClearEditText)
        val clearResId =
            arr.getResourceId(
                R.styleable.ClearEditText_drawable_clear,
                R.drawable.ic_close
            )
        val labelResId =
            arr.getResourceId(R.styleable.ClearEditText_drawable_label, -1)
        drawableClear = ContextCompat.getDrawable(context, clearResId)
        drawableLabel = if (labelResId < 0) {
            null
        } else {
            ContextCompat.getDrawable(context, labelResId)
        }
        setCompoundDrawablesWithIntrinsicBounds(
            drawableLabel,
            compoundDrawables[DRAWABLE_TOP],
            null,
            compoundDrawables[DRAWABLE_BOTTOM]
        )
        arr.recycle()
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        setIconVisible(hasFocus() && length() > 0)
    }

    fun setOnFinishComposingListener(listener: OnFinishComposingListener){
        this.listener = listener
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        return super.onCreateInputConnection(outAttrs)?.let { MyInputConnection(it,false) }
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        setIconVisible(focused && length() > 0)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_UP -> {
                val drawable = compoundDrawables[DRAWABLE_RIGHT]
                if (drawable != null && event.x <= width - paddingRight
                    && event.x >= width - paddingRight - drawable.bounds.width()
                ) {
                    setText("")
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun setIconVisible(visible: Boolean) {
        setCompoundDrawablesWithIntrinsicBounds(
            drawableLabel,
            compoundDrawables[DRAWABLE_TOP],
            if (visible) drawableClear else null,
            compoundDrawables[DRAWABLE_BOTTOM]
        )
    }
    inner class MyInputConnection(target: InputConnection,mutable:Boolean) : InputConnectionWrapper(target,mutable){

        override fun finishComposingText(): Boolean {
            var finishText = super.finishComposingText()
            if(finishText){
                listener?.onFinishComposing(finishText)
            }
            return finishText
        }

    }

    interface OnFinishComposingListener{
        fun onFinishComposing(finishText: Boolean)
    }


    companion object {
        private const val DRAWABLE_LEFT = 0
        private const val DRAWABLE_TOP = 1
        private const val DRAWABLE_RIGHT = 2
        private const val DRAWABLE_BOTTOM = 3
    }
}
