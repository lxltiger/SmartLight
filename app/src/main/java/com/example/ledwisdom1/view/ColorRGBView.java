package com.example.ledwisdom1.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/2/28.
 */
public class ColorRGBView extends View {
    private final static String TAG = "ColorRGBView";
    private Paint mPaintLine;
    private Paint mPaintCircle;
    private OnColorChangedListener mChangedListener;
    public static int ColorText = Color.rgb(255, 0, 0);
    private float mDegree;
    private int startY1, radius1;
    private int radius2 = 0;

    private float mWidth2, mHeight2;
    private int i2 = 2;

    // ### 内部类 ###
    public interface OnColorChangedListener {
        void onColorChanged(int color, int degree);

    }

    public void setColor(int color) {
        mDegree = (float) color;
//        int mDegree = color;
        int[] rgb = getRGB(mDegree);
        mPaintCircle.setARGB(255, rgb[0], rgb[1], rgb[2]);
        invalidate();
    }

    public void setOnColorChangedListenner(OnColorChangedListener listener) {
        mChangedListener = listener;
    }

    public ColorRGBView(Context context) {
        this(context, null);
    }

    public ColorRGBView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    /**
     * ?????
     */
    private void initPaint() {
        mPaintLine = new Paint();
        //???
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(2);

        mPaintCircle = new Paint();
        //???
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setColor(Color.RED);
        mPaintCircle.setStyle(Paint.Style.FILL);
        mPaintCircle.setStrokeWidth(0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float mWidth = getWidth();
        float mHeight = getHeight();
        mWidth2 = mWidth / i2;
        mHeight2 = mHeight / i2;

        int startY1 = (int) (mWidth / 500 * 50);
        int radius1 = startY1;
        int radius2 = (int) (mWidth / 500 * 20);
//        Log.e(TAG, "ColorView onDraw mWidth:" + mWidth + "=====mHeight:" + mHeight);
        //画线
        canvas.rotate(mDegree, mWidth2, mHeight2);
        canvas.drawLine(mWidth2, startY1, mWidth2, mHeight2, mPaintLine);

        //画中心圆
        canvas.drawCircle(mWidth2, mHeight2, radius1, mPaintCircle);
        //画中心圆
        canvas.drawCircle(mWidth2, mHeight2, radius1, mPaintLine);

        //画色盘上的圆
        canvas.drawCircle(mWidth2, startY1, radius2, mPaintCircle);
        //画色盘上的圆
        canvas.drawCircle(mWidth2, startY1, radius2, mPaintLine);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 宽度方向上的测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // 高度方向上的测量模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        /**
         * 判断依据
         * 宽度为Exactly,也就是填充父窗体或者指定宽度
         * 且高度不为Exactly,代表设置的既不是fill_parent也不是具体的值，需要具体测量
         * 且图片宽度比已经赋值完毕，不再是0.0f
         * 表示宽度确定，需要确定高度
         * MeasureSpec类似于ios中的CGSize,比CGSize多了一个mode属性
         */
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
            height = (int) (width / 1 + 0.5f); // 高度实际值(px)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        } else if (widthMode != MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            // 判断依据与上面相反,表示高度确定,需要测量宽度
            width = (int) (height * 1 + 0.5f);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG, "ColorView===x:" + x + ";y:" + y);
//                mDegree = (float)getDegree(mWidth / 2, mHeight / 2-160,mWidth / 2, mHeight / 2,x,y);
                int mDegree = (int) calcAngle(x, y);
//                Log.e(TAG, "mDegree===Degree:" + mDegree);
                int[] rgb = getRGB(mDegree);
                Log.d(TAG, Arrays.toString(rgb));
                mPaintCircle.setARGB(255, rgb[0], rgb[1], rgb[2]);
                ColorText = Color.rgb(rgb[0], rgb[1], rgb[2]);
                setColor(mDegree);
//                postInvalidate();
//                mChangedListener.onColorChanged(ColorText, mDegree);
                break;
        }
        return true;

    }


    public void setmDegree(float mDegree) {
        this.mDegree = mDegree;
    }


    /**
     * 以按钮圆心为坐标圆点，建立坐标系，求出(targetX, targetY)坐标与x轴的夹角
     *
     * @param targetX x坐标
     * @param targetY y坐标
     * @return (targetX, targetY)坐标与x轴的夹角
     */
    private float calcAngle(float targetX, float targetY) {
        float x = targetX - mWidth2;
        float y = targetY - mHeight2;
        double radian;
        if (x != 0) {
            float tan = Math.abs(y / x);
            if (x > 0) {
                if (y >= 0) {
                    radian = Math.atan(tan);
                } else {
                    radian = 2 * Math.PI - Math.atan(tan);
                }
            } else {
                if (y >= 0) {
                    radian = Math.PI - Math.atan(tan);
                } else {
                    radian = Math.PI + Math.atan(tan);
                }
            }
        } else {
            if (y > 0) {
                radian = Math.PI / 2;
            } else {
                radian = -Math.PI / 2;
            }
        }

        return (float) (((radian * 180) / Math.PI) + 90) % 360;
    }

    /**
     * 根据角度获取颜色的值
     *
     * @param degree
     * @return
     */
    public int[] getRGB(float degree) {
        int[] rgb = new int[3];
        if (0 <= degree && degree <= 60) {
            rgb[0] = 255;
            rgb[1] = (int) (degree / 60 * 255);
            rgb[2] = 0;
        } else if (60 < degree && degree <= 120) {
            rgb[0] = 255 - (int) ((degree - 60) / 60 * 255);
            rgb[1] = 255;
            rgb[2] = 0;
        } else if (120 < degree && degree <= 180) {
            rgb[0] = 0;
            rgb[1] = 255;
            rgb[2] = (int) ((degree - 120) / 60 * 255);
        } else if (180 < degree && degree <= 240) {
            rgb[0] = 0;
            rgb[1] = 255 - (int) ((degree - 180) / 60 * 255);
            rgb[2] = 255;
        } else if (240 < degree && degree <= 300) {
            rgb[0] = (int) ((degree - 240) / 60 * 255);
            rgb[1] = 0;
            rgb[2] = 255;
        } else if (300 < degree && degree <= 360) {
            rgb[0] = 255;
            rgb[1] = 0;
            rgb[2] = 255 - (int) ((degree - 300) / 60 * 255);
        }
        return rgb;
    }

}
