package com.syt.ttstep.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

//两种计步传感器的公共父类
public interface CommonListener extends SensorEventListener {


    public void setStepsCount(int step);

    public long getmLimits();

    public void setmLimits(long mLimits) ;


    public float getSensitivity();

    public int getStepsCount();

    public void setSensitivity(float sensitivity) ;


}
