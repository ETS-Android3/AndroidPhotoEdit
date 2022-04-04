package com.yunianshu.library.view;

import android.app.Dialog;
import android.content.Context;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.LogUtils;
import com.yunianshu.library.R;

/**
 * @author 请叫我张懂
 * @createTime 2018/8/22 09:49
 * @description
 */

public class ModifyContentDialog extends Dialog {
    private ClearEditText etInputContent;
    private OnTextChangedListener onTextChangedListener;
    private Context context;
    private boolean sustained = true;

    public ModifyContentDialog setOnTextChangedListener(OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
        return this;
    }

    public ModifyContentDialog setOnTextChangedListener(boolean sustained,OnTextChangedListener onTextChangedListener) {
        this.onTextChangedListener = onTextChangedListener;
        this.sustained = sustained;
        return this;
    }

    public ModifyContentDialog(@NonNull Context context) {
        super(context, R.style.BaseKDialog);
        this.context = context;
        setContentView(R.layout.edit_dialog_modify_content);
        initView();
    }

    private void initView() {
        etInputContent = this.findViewById(R.id.edit_et_input_content);
        //
        setCanceledOnTouchOutside(true);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        Window window = this.getWindow();
        if (window != null) {
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = displayMetrics.widthPixels;
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT;
            attributes.gravity = Gravity.BOTTOM;
            attributes.dimAmount = 0f;
            attributes.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            window.setWindowAnimations(R.style.EditInputContentDialogAnim);
            window.setAttributes(attributes);
        } else {
            throw new NullPointerException("dialog.getWindow() is null");
        }
        //
        findViewById(R.id.edit_iv_finish).setOnClickListener(v ->{
            if(!sustained){//点击确定才返回内容
                if (onTextChangedListener != null) {
                    onTextChangedListener.onTextChange(etInputContent.getText().toString());
                }
            }
            KeyboardUtils.hideSoftInput(etInputContent);
            cancel();
        });
        setOnShowListener(dialog ->
                KeyboardUtils.showSoftInput(etInputContent));
        etInputContent.addTextChangedListener(new CommonTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(sustained){
                    if (onTextChangedListener != null) {
                        onTextChangedListener.onTextChange(s);
                    }
                }
            }
        });
    }

    public ModifyContentDialog setInputType(int inputType, InputFilter[] inputFilters) {
        etInputContent.setInputType(inputType);
        if (inputFilters != null) {
            etInputContent.setFilters(inputFilters);
        }
        return this;
    }

    public ModifyContentDialog setInputType(InputFilter[] inputFilters) {
        if (inputFilters != null) {
            etInputContent.setFilters(inputFilters);
        }
        return this;
    }

    @Override
    public void dismiss() {
        KeyboardUtils.hideSoftInput(etInputContent);
        super.dismiss();
    }

    public ModifyContentDialog setContent(String content) {
        etInputContent.setText(content);
        if (!TextUtils.isEmpty(content)) {
            try {
                etInputContent.setSelection(content.length());
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }
        return this;
    }

    public ModifyContentDialog setHint(@StringRes int strId) {
        etInputContent.setHint(strId);
        return this;
    }

    public interface OnTextChangedListener {
        void onTextChange(CharSequence charSequence);
    }
}
