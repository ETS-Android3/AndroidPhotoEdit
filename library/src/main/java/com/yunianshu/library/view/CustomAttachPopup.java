package com.yunianshu.library.view;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.lxj.xpopup.core.HorizontalAttachPopupView;
import com.yunianshu.library.R;

public class CustomAttachPopup extends HorizontalAttachPopupView {

    public Base setListener(OnItemClickListener listener) {
        this.listener = listener;

    }

    private OnItemClickListener listener;

    public CustomAttachPopup(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.custom_attach_popup;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.tv_zan).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(0);
                }
                dismiss();
            }
        });
        findViewById(R.id.tv_comment).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onItemClick(1);
                }
                dismiss();
            }
        });
    }

    interface OnItemClickListener{

        void onItemClick(int pos);
    }

}