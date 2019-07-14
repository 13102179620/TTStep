package com.syt.ttstep.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.syt.ttstep.beans.PedometerBean;

/**
 * sdk19之前，需要使用加速度传感器去计算步伐
 * */
public class PedometerListenr implements SensorEventListener {
    //步数
    private int stepsCount = 0;
    //灵敏度（相对于diff而言）,这个值与漂移，放大倍数相关 ，需要多次调整
    private float sensitivity = 30;
    //最大限制
    private long mLimits;
    //最后保存的数值
    private float mLastValue;
    //信号放大
    private float mScale = -4f;
    //信号偏移
    private float offset = 240;
    //采样时间
    private long start = 0;
    private long end = 0;

    //上一次传感器变化的方向
    private float mLastDirection;
    //数值记录
    private float mLastExtrems[][] = new float[2][1];
    //上一次传感器变化量
    private float mLastDiff;
    //上一次是否有效
    private int mLastMatch = -1;

    private Object Lock = new Object();
    private PedometerBean data;

    public PedometerListenr(PedometerBean data){
        this.data = data;
    }

    public PedometerListenr(Context context, PedometerBean pedmoeterBean)
    {
        super();
        int h = 480;
        offset = h * 0.5f;//240
        mScale = -(h * 0.5f * (1.0f / (SensorManager.STANDARD_GRAVITY * 2)));

        this.data = pedmoeterBean;
    }

    public void setStepsCount(int step){
        stepsCount = step;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        synchronized (Lock){

            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                float sum = 0;
                for (int i = 0; i < 3; i++) {
                    float vector = offset + sensorEvent.values[i] * mScale;
                    sum += vector;
                }
                float average = sum/3;
                float direction;
                //规定方向（2种）
                if (average > mLastValue) {
                    direction = 1;
                }else if(average < mLastValue) {
                    direction = -1;
                }else {
                    direction = 0;
                }

                if (direction == -mLastDirection){

                    int extType = (direction > 0 ? 0 : 1);
                    mLastExtrems[extType][0] = mLastValue;

                    //向量变化绝对值
                    float diff = Math.abs(mLastExtrems[extType][0] - mLastExtrems[1 - extType][0]);

                    //过滤太微小的向量
                    if (diff > sensitivity){
                        boolean isLargeEnough = diff > (mLastDiff * 2/3);
                        boolean isPreLargeEnough = mLastDiff > (diff/3);
                        //方向是否是变化的
                        boolean isDifferentDirect  =(mLastMatch != 1 - extType);

                        if (isLargeEnough && isPreLargeEnough && isDifferentDirect){
                            end = System.currentTimeMillis();
                            //如果这次向量变化有效
                            if ( end - start > mLimits){
                                 stepsCount++;
                                 mLastMatch = extType;
                                 start = end;
                                 mLastDiff = diff;

                                 if (data != null){
                                     //保存数据
                                     data.setStepsCount(stepsCount);
                                     data.setLastStepTime(System.currentTimeMillis());

                                 }

                            }else{ //
                                mLastDiff = sensitivity;
                            }
                        }else {
                            mLastMatch = -1;
                            mLastDiff = sensitivity;
                        }
                    }
                }

                mLastDirection = direction;
                mLastValue = average;

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    public long getmLimits() {
        return mLimits;
    }

    public void setmLimits(long mLimits) {
        this.mLimits = mLimits;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
