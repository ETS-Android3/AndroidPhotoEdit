package com.yunianshu.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatImageView;

public class AlbumImageView extends AppCompatImageView {
    /**
     * 合成图片类型
     */
    public int albumImageType = 0;
    // 图片类型: 形状
    public static final int ALBUM_IMAGE_SHAPE = 1001;
    // 图片类型: 带相框的高大上图片
    public static final int ALBUM_IMAGE_FRAME = 1002;
    /**
     * 图片模板: 形状的只有1个   带相框的则有两个(一个是相框,一个是相框的阴影截取范围)
     */
    private Bitmap[] bitmapMask = new Bitmap[2];
    // 原图片
    private Bitmap src;
    // x轴偏移
    private float xOffset = 0.f;
    //y轴偏移
    private float yOffset = 0.f;
    //手指所在x坐标
    private float downX = 0.0f;
    //手指所在y坐标
    private float downY = 0.0f;

    private float previousDistance = 0.0f;
    private PointF midPoint = new PointF();


    public AlbumImageView(Context context, int imageType, Bitmap src, Bitmap[] bitmapMask, float xOffset, float yOffset) {
        super(context);
        this.albumImageType = imageType;
        this.bitmapMask = bitmapMask;
        this.src = src;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        init();
    }

    public AlbumImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * 初始化处理原图片 保证图片不能小于相框或形状
     */
    private void init() {
        //如果原图片宽小于模板宽 按照等比例拉伸原图片
        if (src.getWidth() < bitmapMask[0].getWidth()) {
            Matrix matrix = new Matrix();
            matrix.postScale((float) bitmapMask[0].getWidth() / (float) src.getWidth(), (float) bitmapMask[0].getWidth() / (float) src.getWidth());
            src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        //如果原图片高小于模板搞 按照等比例拉伸原图片
        if (src.getHeight() < bitmapMask[0].getHeight()) {
            Matrix matrix2 = new Matrix();
            matrix2.postScale((float) bitmapMask[0].getHeight() / (float) src.getHeight(), (float) bitmapMask[0].getHeight() / (float) src.getHeight());
            src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix2, true);
        }
        //设置Bitmap
        this.setImageBitmap(resolveBitmap(-xOffset, -yOffset));
    }

    /**
     * 处理图片 合成图片
     *
     * @param xOffset x轴偏移量
     * @param yOffset y轴偏移量
     * @return 返回合成之后的Bitmap
     */
    private Bitmap resolveBitmap(float xOffset, float yOffset) {
        //用模板生成一个bmp
        Bitmap bmp = Bitmap.createBitmap(bitmapMask[0].getWidth(), bitmapMask[0].getHeight(), Bitmap.Config.ARGB_8888);
        //初始化画笔
        Paint paint = new Paint();
        if (albumImageType == ALBUM_IMAGE_SHAPE) {
            //PorterDuffXfermode算法  SRC_ATOP 取下层非交集部分与上层交集部分
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            Canvas canvas = new Canvas(bmp);
            // 画模板
            canvas.drawBitmap(bitmapMask[0], 0, 0, null);
            //画原图
            canvas.drawBitmap(src, xOffset, yOffset, paint);
            return bmp;
        } else {
            //DST_ATOP 取上层非交集部分与下层交集部分
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
            Canvas canvas = new Canvas(bmp);
            canvas.drawBitmap(bitmapMask[0], 0, 0, null);
            canvas.drawBitmap(src, xOffset, yOffset, paint);
            // 相框阴影部分图片
            Bitmap result = Bitmap.createBitmap(bitmapMask[1].getWidth(), bitmapMask[1].getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas1 = new Canvas(result);
            Paint paint1 = new Paint();
            // 然后再画阴影部分 SRC_OUT 取上层非交集部分 抠出相框和图片
            paint1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            canvas1.drawBitmap(bitmapMask[1], 0, 0, null);
            canvas1.drawBitmap(bmp, 0, 0, paint1);
            bmp.recycle();
            return result;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("gxh", "aaa");
        if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int offsetX = -((int) downX - (int) event.getX());
                    int offsetY = -((int) downY - (int) event.getY());
                    xOffset += offsetX;
                    yOffset += offsetY;
                    //判断是否滑到边界
                    if ((xOffset <= 0 && yOffset <= 0) && (src.getWidth() - Math.abs(xOffset) > bitmapMask[0].getWidth() && src.getHeight() - Math.abs(yOffset) > bitmapMask[0].getHeight())) {
                        this.setImageBitmap(resolveBitmap(xOffset, yOffset));
                    } else {
                        //else 里面判断xy某一坐标滑到边界  另一个坐标还得继续移动 否则会出现问题
                    if (xOffset > 0) {
                        xOffset = 0;
                    }

                    if (yOffset > 0) {
                        yOffset = 0;
                    }

                    if (src.getWidth() - Math.abs(xOffset) < bitmapMask[0].getWidth()) {
                        xOffset = bitmapMask[0].getWidth() - src.getWidth();
                    }
                    if (src.getHeight() - Math.abs(yOffset) < bitmapMask[0].getHeight()) {
                        yOffset = bitmapMask[0].getHeight() - src.getHeight();
                    }
                        setImageBitmap(resolveBitmap(xOffset, yOffset));
                    }
                    downX = event.getX();
                    downY = event.getY();
                    break;
                default:
                    break;
            }
        } else {
            //todo 多点触控
//            switch (event.getAction() & MotionEvent.ACTION_MASK) {
//                case MotionEvent.ACTION_POINTER_DOWN:
//                    previousDistance = (float) Math.sqrt(square(event.getX(0) - event.getX(1)) + square(event.getY(0) - event.getY(1)));
//                    calculateMidPoint(event, midPoint);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    int currentDistance = (int) Math.sqrt(square(event.getX(0) - event.getX(1)) + square(event.getY(0) - event.getY(1)));
//                    Matrix matrix = new Matrix();
//                    if (previousDistance > 0) {
//                        float scale = currentDistance / previousDistance;
//                        if(scale > 3){
//                            scale = 3;
//                        }else if(scale < 1){
//                            scale = 1f;
//                        }
//                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
//                        if (albumImageType == ALBUM_IMAGE_SHAPE) {
//                            Bitmap bmp = Bitmap.createBitmap(bitmapMask[0].getWidth(), bitmapMask[0].getHeight(), Bitmap.Config.ARGB_8888);
//                            //初始化画笔
//                            Paint paint = new Paint();
//                            //PorterDuffXfermode算法  SRC_ATOP 取下层非交集部分与上层交集部分
//                            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
//                            Canvas canvas = new Canvas(bmp);
//                            // 画模板
//                            canvas.drawBitmap(bitmapMask[0], 0, 0, null);
//                            //画原图
//                            canvas.drawBitmap(src, matrix, paint);
//                            setImageBitmap(bmp);
//
//                            Bitmap bmp1 = Bitmap.createScaledBitmap(src, (int) (src.getWidth() * scale),
//                                    (int) (src.getHeight() * scale), true);
//                            src = bmp1.copy(Bitmap.Config.ARGB_8888, true);
//                            bmp1.recycle();
//                        }
//                    }
//                    break;
//                case MotionEvent.ACTION_UP:
//                    break;
//                default:
//                    break;
//            }
        }

        return true;
    }

    private void calculateMidPoint(MotionEvent event, PointF point) {
        point.x = (event.getX(0) + event.getX(1)) / 2;
        point.y = (event.getY(0) + event.getY(1)) / 2;
    }

    //求平方
    public double square(float x) {
        return x * x;
    }
}
