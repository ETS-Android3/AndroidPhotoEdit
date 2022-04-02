package com.yunianshu.sticker;

import android.view.MotionEvent;

/**
 * Create by WingGL
 * createTime: 2022/4/1
 */
public class CopyIconEvent implements StickerIconEvent{
    @Override
    public void onActionDown(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionMove(StickerView stickerView, MotionEvent event) {

    }

    @Override
    public void onActionUp(StickerView stickerView, MotionEvent event) {
        stickerView.copySticker();
    }
}
