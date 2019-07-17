package com.syt.ttstep.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.syt.ttstep.beans.PedometerBean;


/**
 * sdk版本大于19后，可以直接使用计步传感器TYPE_STEP_DETECTOR
 */
public class CountStepListner implements CommonListener {

    private boolean hasRecord;
    private int stepsCount = -1;
    private PedometerBean data;


    public CountStepListner(PedometerBean data) {
        this.data = data;
    }


    public CountStepListner(Context context, PedometerBean pedmoeterBean) {
        super();
        this.data = pedmoeterBean;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
            Log.d("jibuqi", "startSetpsCount: 获取计步信息！" + sensor.getName() + " | " + event.values[0]);
            //只有一个返回值 1.0f, 表示一次计步
            if (event.values[0] >= 1.0f) {
                stepsCount++;
                data.setStepsCount(stepsCount);
                data.setLastStepTime(System.currentTimeMillis());
            }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void setStepsCount(int step) {
        stepsCount = step;
    }


    public int getStepsCount() {
        return stepsCount;
    }

    @Override
    public long getmLimits() {
        return 0;
    }

    @Override
    public void setmLimits(long mLimits) {

    }

    @Override
    public float getSensitivity() {
        return 0;
    }

    @Override
    public void setSensitivity(float sensitivity) {

    }


}
