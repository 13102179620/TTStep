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
    //最大限制,与采样时间相对应
    private long mLimits;
    //最后保存的数值
    private float mLastValue;
    //信号放大
    private float mScale = -5f;
    //信号偏移，消除信号符号的影响
    private float offset = 200f;
    //采样时间
    private long start = 0;
    private long end = 0;

    //上一次传感器变化的方向
    private float mLastDirection;
    //数值记录，记录上次和本次
    private float mLastExtrems[] = new float[2];

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
        this.data = pedmoeterBean;
    }

    //主要用来重置清零
    public void setStepsCount(int step){
        stepsCount = step;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sensor = sensorEvent.sensor;
        synchronized (Lock){
            if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                float sum = 0;
                //三向平均变化量
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
                //与上次方向相反，才算有效
                if (direction == -mLastDirection){
                    //规定01
                    int extType = (direction > 0 ? 0 : 1);
                    mLastExtrems[extType] = mLastValue;

                    //向量变化绝对值 这次-上次  也可以不加abs 因为extType保存了符号信息
                    float diff = Math.abs(mLastExtrems[extType] - mLastExtrems[1 - extType]);

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
                                     //对传入的databean赋值
                                     data.setStepsCount(stepsCount);
                                     data.setLastStepTime(System.currentTimeMillis());

                                 }

                            }else{
                                //恢复初始化
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
