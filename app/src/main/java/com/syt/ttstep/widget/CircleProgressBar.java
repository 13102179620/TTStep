package com.syt.ttstep.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;



public class CircleProgressBar extends View {

    public static final String KEY_PROGRESS = "progress";
    private static final String INSTANCE = "instance";
    private int progress = 0;
    private int maxProgress = 100;
    //边界画笔
    private Paint pathPaint;
    //填充画笔
    private Paint fillPait;
    //椭圆所在矩形区域
    private RectF oval;
    //渐变色样式
    private int[] arcCloros = {0xFF02c016 , 0xFF3DF346, 0xFF40F1D5 , 0xFF02C016};

    //背景色
    private int pathColor = 0xFFF0EEDF;
    //边框色
    private int borderColor = 0xFFD2D1C4;
    private int pathWidth = 35;
    private int height;
    private int width;
    private int radius = 120;

    //渐变色绘制
    private SweepGradient sweepGradient;

    private boolean reset = false;


    public CircleProgressBar(Context context,  AttributeSet attrs) {
        super(context, attrs);
        pathPaint = new Paint();
        pathPaint.setAntiAlias(true);
        pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStyle(Paint.Style.STROKE);
        //设置防抖动，渐变柔和
        pathPaint.setDither(true);
        //线头连接处是圆角
        pathPaint.setStrokeJoin(Paint.Join.ROUND);

        fillPait = new Paint();
        fillPait.setAntiAlias(true);
        fillPait.setFlags(Paint.ANTI_ALIAS_FLAG);
        fillPait.setStyle(Paint.Style.STROKE);
        //设置防抖动，渐变柔和
        fillPait.setDither(true);
        //线头连接处是圆角
        fillPait.setStrokeJoin(Paint.Join.ROUND);

        oval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reset){
            canvas.drawColor(0xFFFFFFFF);
            reset = false;
        }
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        radius = getMeasuredWidth()/2 - pathWidth;
        pathPaint.setColor(pathColor);
        pathPaint.setStrokeWidth(pathWidth);
        //画圆
        canvas.drawCircle(width/2 , height/2 , radius , pathPaint);
        //细边框宽度
        pathPaint.setStrokeWidth(0.5f);
        pathPaint.setColor(borderColor);
        //外边框
        canvas.drawCircle(width/2,height/2 ,(float)radius+pathWidth/2 , pathPaint);
        //内边框
        canvas.drawCircle(width/2,height/2 ,(float)radius-pathWidth/2 , pathPaint);

        sweepGradient = new SweepGradient((float)(width/2) , (float)(height/2) , arcCloros , null);

        fillPait.setShader(sweepGradient);
        //线帽为圆角
        fillPait.setStrokeCap(Paint.Cap.ROUND);
        fillPait.setStrokeWidth(pathWidth);
        this.oval.set((float) (width / 2 - radius), (float) (height / 2 - radius), (float) (width / 2 + radius), (float) (height / 2 + radius));
        //根据当前进度绘制bar
        canvas.drawArc(oval , -90.0F , ((float)progress/(float) maxProgress)*360.0F , false , fillPait);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightsize = MeasureSpec.getSize(heightMeasureSpec);
        int widthsize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        setMeasuredDimension(widthsize , heightsize);
    }


    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
        invalidate();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        //更新view
        invalidate();
    }

    public int getPathColor() {
        return pathColor;
    }

    public void setPathColor(int pathColor) {
        this.pathColor = pathColor;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getPathWidth() {
        return pathWidth;
    }

    public void setPathWidth(int pathWidth) {
        this.pathWidth = pathWidth;
    }



    public void setReset(boolean reset) {
        this.reset = reset;
        if (reset){
            progress = 0;
            //使该view 无效， 再出发ondraw重新绘制
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PROGRESS, progress);
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        return bundle;
    }

    //缓存数据，下次打开正常显示
    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if (state instanceof Bundle)
        {
            Bundle bundle = (Bundle) state;
            Parcelable parcelable = bundle.getParcelable(INSTANCE);
            super.onRestoreInstanceState(parcelable);
            progress = bundle.getInt(KEY_PROGRESS);
            return;
        }
        super.onRestoreInstanceState(state);
    }
}
