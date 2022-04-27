package com.yunianshu.library.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class MyImageView extends View {

    private Paint mPaint;

    private Bitmap bitmap;

    private Bitmap frame;

    private float frameWidth;

    private float frameHeight;

    private Matrix matrix;

// 平移开始点与移动点

    private Point startPoint;

    private Point movePoint;

    private float initDistance;

// 记录当前平移距离

    private int sx;

    private int sy;

// 保存平移状态

    private int oldsx;

    private int oldsy;

// scale rate

    private float widthRate;

    private float heightRate;

    public MyImageView(Context context) {

        super(context);

    }

    public MyImageView(Context context, AttributeSet attrs) {

        super(context, attrs);

    }

    public void setBitmap(Bitmap bitmap) {

        this.bitmap = bitmap;

    }

    public void setFrame(Bitmap bitmap) {
        this.frame = bitmap;
    }

    public void setFrame(Bitmap bitmap,float width,float height) {
        this.frame = bitmap;
        this.frameWidth = width;
        this.frameHeight = height;
    }

    private void initParameters() {

// 初始化画笔

        mPaint = new Paint();

        mPaint.setColor(Color.BLACK);

        matrix = new Matrix();

        if (bitmap != null) {

            float iw = bitmap.getWidth();

            float ih = bitmap.getHeight();

            float width = this.getWidth();

            float height = this.getHeight();

// 初始放缩比率

            widthRate = width / iw;

            heightRate = height / ih;

        }

        sx = 0;

        sy = 0;

        oldsx = 0;

        oldsy = 0;

    }

    public void setStartPoint(Point startPoint) {

        this.startPoint = startPoint;

    }

    public void setInitDistance(float initDistance) {

        this.initDistance = initDistance;

    }

    public void zoomIn(float distance) {

        float rate = distance / this.initDistance;

        float iw = bitmap.getWidth();

        float ih = bitmap.getHeight();

        float width = this.getWidth();

        float height = this.getHeight();

// get scale rate

        widthRate = (width / iw) * rate;

        heightRate = (height / ih) * rate;

// make it same as view size

        float iwr = (width / iw);

        float ihr = (height / ih);

        if (iwr >= widthRate) {

            widthRate = (width / iw);

        }

        if (ihr >= heightRate) {

            heightRate = (height / ih);

        }

// go to center

        oldsx = (int) ((width - widthRate * iw) / 2);

        oldsy = (int) ((height - heightRate * ih) / 2);

    }

    public void setMovePoint(Point movePoint) {

        this.movePoint = movePoint;

        sx = this.movePoint.x - this.startPoint.x;

        sy = this.movePoint.y - this.startPoint.y;

        float iw = bitmap.getWidth();

        float ih = bitmap.getHeight();

// 检测边缘

        int deltax = (int) ((widthRate * iw) - this.getWidth());

        int deltay = (int) ((heightRate * ih) - this.getHeight());

        if ((sx + this.oldsx) >= 0) {

            this.oldsx = 0;

            sx = 0;

        } else if ((sx + this.oldsx) <= -deltax) {

            this.oldsx = -deltax;

            sx = 0;

        }

        if ((sy + this.oldsy) >= 0) {

            this.oldsy = 0;

            this.sy = 0;

        } else if ((sy + this.oldsy) <= -deltay) {

            this.oldsy = -deltay;

            this.sy = 0;

        }

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

    @Override

    protected void onDraw(Canvas canvas) {

        if (matrix == null) {

            initParameters();

        }

        if (bitmap != null) {

            matrix.reset();

            matrix.postScale(widthRate, heightRate);

            matrix.postTranslate(oldsx + sx, oldsy + sy);

            canvas.drawBitmap(bitmap, matrix, mPaint);

            canvas.drawBitmap(frame,0,0,null);


        } else {

// fill rect

            Rect rect = new Rect(0, 0, getWidth(), getHeight());

            mPaint.setAntiAlias(true);

            mPaint.setColor(Color.BLACK);

            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

            canvas.drawRect(rect, mPaint);

        }

    }


    public Bitmap saveBitmap(){
        Bitmap tmp = Bitmap.createBitmap(frame.getWidth(),frame.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tmp);
        matrix.reset();

        matrix.postScale(widthRate, heightRate);

        matrix.postTranslate(oldsx + sx, oldsy + sy);

        canvas.drawBitmap(bitmap, matrix, new Paint());

        canvas.drawBitmap(frame,0,0,null);
        return tmp;
    }

}

