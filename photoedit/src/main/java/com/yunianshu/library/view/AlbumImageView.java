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
    private Bitmap[] bitmapMask = new Bitmap[2];
    // 原图片
    private Bitmap src;
    // x轴偏移
    private float xOffset = 0.f;
    //y轴偏移
    private float yOffset = 0.f;
    // 手指所在x坐标
    private float downX = 0.0f;
    // 手指所在y坐标
    private float downY = 0.0f;

    private float previousDistance = 0.0f;
    private PointF midPoint = new PointF();

    private Bitmap result;

    // 记录当前平移距离

    private float sx;

    private float sy;

// 保存平移状态

    private float oldsx;

    private float oldsy;

// scale rate

    private float widthRate = 1.0f;

    private float heightRate = 1.0f;

    // 平移开始点与移动点

    private PointF startPoint;

    private PointF movePoint;

    //事件状态  0-null  1-单指移动  2-双指移动
    private int eventState = 0;


    public AlbumImageView(Context context, int imageType, Bitmap src, Bitmap[] bitmapMask, float xOffset, float yOffset) {
        super(context);
        this.albumImageType = imageType;
        this.bitmapMask = bitmapMask;
        this.src = src;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
//        init();
        initParameters();
        Matrix matrix = new Matrix();
        matrix.postScale(widthRate, heightRate);
        matrix.postTranslate(oldsx + sx, oldsy + sy);
        Bitmap bmp = Bitmap.createBitmap(bitmapMask[0].getWidth(), bitmapMask[0].getHeight(), Bitmap.Config.ARGB_8888);
        //初始化画笔
        Paint paint = new Paint();
        //PorterDuffXfermode算法  SRC_ATOP 取下层非交集部分与上层交集部分
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
        Canvas canvas = new Canvas(bmp);
        // 画模板
        canvas.drawBitmap(bitmapMask[0], 0, 0, null);
        //画原图
        canvas.drawBitmap(src, matrix, paint);
        setImageBitmap(bmp);
        this.result = bmp.copy(Bitmap.Config.ARGB_8888, true);
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
            float sx = bitmapMask[0].getWidth() / src.getWidth();
            widthRate = sx;
            heightRate = sx;
            matrix.postScale(sx, sx);
            src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
        }
        //如果原图片高小于模板搞 按照等比例拉伸原图片
        if (src.getHeight() < bitmapMask[0].getHeight()) {
            Matrix matrix2 = new Matrix();
            float sx = bitmapMask[0].getHeight() / src.getHeight();
            widthRate = sx;
            heightRate = sx;
            matrix2.postScale(sx, sx);
            src = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix2, true);
        }
        //设置Bitmap
        this.setImageBitmap(resolveBitmap(-xOffset, -yOffset));
    }

    private void initParameters() {


        float iw = src.getWidth();

        float ih = src.getHeight();

        float width = bitmapMask[0].getWidth();

        float height = bitmapMask[0].getHeight();

// 初始放缩比率

        widthRate = width / iw;

        heightRate = widthRate;

        if (ih * heightRate < height) {
            heightRate = height / ih;
            widthRate = heightRate;
        }


        sx = 0;

        sy = 0;

        oldsx = (width - widthRate * iw) / 2;

        oldsy = (height - heightRate * ih) / 2;

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
            this.result = bmp.copy(Bitmap.Config.ARGB_8888, true);
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
            this.result = result.copy(Bitmap.Config.ARGB_8888, true);
            return result;
        }
    }

    public Bitmap getResult() {
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("gxh", "aaa");
        if (event.getPointerCount() == 1) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getX();
                    downY = event.getY();
                    startPoint = new PointF(downX, downY);
                    eventState = 1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    setMovePoint(new PointF(event.getX(), event.getY()));
                    Matrix matrix = new Matrix();
                    matrix.postScale(widthRate, heightRate);
                    matrix.postTranslate(oldsx + sx, oldsy + sy);
                    Bitmap bmp = Bitmap.createBitmap(bitmapMask[0].getWidth(), bitmapMask[0].getHeight(), Bitmap.Config.ARGB_8888);
                    //初始化画笔
                    Paint paint = new Paint();
                    //PorterDuffXfermode算法  SRC_ATOP 取下层非交集部分与上层交集部分
                    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
                    Canvas canvas = new Canvas(bmp);
                    // 画模板
                    canvas.drawBitmap(bitmapMask[0], 0, 0, null);
                    //画原图
                    canvas.drawBitmap(src, matrix, paint);
                    setImageBitmap(bmp);
                    this.result = bmp.copy(Bitmap.Config.ARGB_8888, true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_OUTSIDE:
                    savePreviousResult();
                    eventState = 0;
                    break;
                default:
                    break;
            }
        } else {
            //todo 多点触控
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    previousDistance = sqrt(event);
                    eventState = 2;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float currentDistance = sqrt(event);
                    zoomIn(currentDistance);
                    Matrix matrix = new Matrix();
                    if (previousDistance > 0) {
                        matrix.postScale(widthRate, heightRate);
                        matrix.postTranslate(oldsx + sx, oldsy + sy);
                        Bitmap bmp = Bitmap.createBitmap(bitmapMask[0].getWidth(), bitmapMask[0].getHeight(), Bitmap.Config.ARGB_8888);
                        //初始化画笔
                        Paint paint = new Paint();
                        //PorterDuffXfermode算法  SRC_ATOP 取下层非交集部分与上层交集部分
                        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
                        Canvas canvas = new Canvas(bmp);
                        // 画模板
                        canvas.drawBitmap(bitmapMask[0], 0, 0, null);
                        //画原图
                        canvas.drawBitmap(src, matrix, paint);
                        setImageBitmap(bmp);
                        this.result = bmp.copy(Bitmap.Config.ARGB_8888, true);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    savePreviousResult();
                    eventState = 0;
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    public void zoomIn(float distance) {

        float rate = distance / this.previousDistance;

        float iw = src.getWidth();

        float ih = src.getHeight();

        float width = this.getWidth();

        float height = this.getHeight();

        // get scale rate
        widthRate = (width / iw) * rate;
        heightRate = widthRate;

//        heightRate = (height / ih) * rate;

        // make it same as view size
        float iwr = (width / iw);

        float ihr = (height / ih);

//        if (iwr >= widthRate) {
//
//            widthRate = (width / iw);
//            heightRate = widthRate;
//        }

//        if (ihr >= heightRate) {
//
//            heightRate = (height / ih);
//            widthRate = heightRate;
//        }

        // go to center
        oldsx = (width - widthRate * iw) / 2;

        oldsy = (height - heightRate * ih) / 2;

    }


    public void setMovePoint(PointF movePoint) {

        this.movePoint = movePoint;

        sx = this.movePoint.x - this.startPoint.x;

        sy = this.movePoint.y - this.startPoint.y;

        float iw = src.getWidth();

        float ih = src.getHeight();

        // 检测边缘
//        int deltax = (int) ((widthRate * iw) - this.getWidth());
//
//        int deltay = (int) ((heightRate * ih) - this.getHeight());
//
//        if ((sx + this.oldsx) >= 0) {
//
//            this.oldsx = 0;
//
//            sx = 0;
//
//        } else if ((sx + this.oldsx) <= -deltax) {
//
//            this.oldsx = -deltax;
//
//            sx = 0;
//
//        }
//
//        if ((sy + this.oldsy) >= 0) {
//
//            this.oldsy = 0;
//
//            this.sy = 0;
//
//        } else if ((sy + this.oldsy) <= -deltay) {
//
//            this.oldsy = -deltay;
//
//            this.sy = 0;
//
//        }

        float width = this.getWidth();

// 初始放缩比率

        float iwr = width / iw;

        if (iwr == widthRate) {

            sx = 0;

            sy = 0;

            oldsx = 0;

            oldsy = 0;

        }

    }

    public void savePreviousResult() {

        this.oldsx = this.sx + this.oldsx;

        this.oldsy = this.sy + this.oldsy;

// zero

        sx = 0;

        sy = 0;

    }

    private void calculateMidPoint(MotionEvent event, PointF point) {
        point.x = (event.getX(0) + event.getX(1)) / 2;
        point.y = (event.getY(0) + event.getY(1)) / 2;
    }

    //求平方
    public double square(float x) {
        return x * x;
    }

    /**
     * 求两点之间的距离
     *
     * @param event
     */
    public float sqrt(MotionEvent event) {
        return (float) Math.sqrt(square(event.getX(0) - event.getX(1)) + square(event.getY(0) - event.getY(1)));
    }
}
