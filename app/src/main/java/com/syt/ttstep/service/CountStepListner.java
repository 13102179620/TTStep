package com.syt.ttstep.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


/**
 * sdk版本大于19后，可以直接使用计步传感器TYPE_STEP_DETECTOR
 * */
public class CountStepListner implements SensorEventListener {

    private boolean hasRecord;
    private int stepsCount = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_STEP_DETECTOR){
            //只有一个返回值 1.0f, 表示一次计步
            if (event.values[0] == 1.0f)
                stepsCount++;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
