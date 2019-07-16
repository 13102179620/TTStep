package com.syt.ttstep.Settings;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.syt.ttstep.frame.PrefsManager;

/**
 * @class name：Settings
 * @describe 设置传感器的灵敏度，采样时间间隔 ，以及用户步长、体重
 * @author syt
 * @time 2019/7/11
 */
public class Settings {
    public static final String TAG = "Setting-app";
    public static final float[] SENSITIVE_ARRAY = {1.5f, 3.0f, 4.0f, 6.0f, 10.0f, 15.0f, 20.0f, 30.0f, 50.0f};
    //采样间隔最高100ms
    public static final int[] INTERVAL_ARRAY = {100, 200, 300, 400, 500, 600, 700, 800};
    public static final String SENSITIVITY = "sensitivity";
    public static final String INTERVAL = "interval";
    public static final String STEP_LEN = "steplen";
    public static final String BODY_WEIGHT = "bodyweight";
    PrefsManager prefsManager = null;

    public Settings(Context context){
        prefsManager = new PrefsManager(context);
    }

    public double getSensitivity(){
        float sensitivity = prefsManager.getFloat(SENSITIVITY);
        if (sensitivity == 0.0f){
            Log.d(TAG, "getSensitivity: 传感器灵敏度为默认灵敏敏度10.0f" );
            return 10.0f;
        }
        Log.d(TAG, "getSensitivity: 传感器灵敏度为：" + sensitivity);
        return sensitivity;
    }
    //设置信息存入pf
    public void setSensitivity(float sensitivity){
        prefsManager.putFloat(SENSITIVITY , sensitivity);
    }


    public int getInterval(){
        int interval = prefsManager.getInt(INTERVAL);
        if (interval == 0)
            return 200;

        Log.d(TAG, "getInterval: 获取时间间隔：" + interval);
        return interval;
    }
    //设置信息存入pf
    public void setInterval(int interval)
    {
        prefsManager.putInt(INTERVAL, interval);
    }

    public float getSetpLength()
    {
        float stepLength = prefsManager.getFloat(STEP_LEN);
        if (stepLength == 0.0f)
        {
            return 50.0f;
        }
        Log.d(TAG, "getSetpLength: 获取用户步长：" + stepLength);
        return stepLength;
    }

    public void setStepLength(float stepLength)
    {
        prefsManager.putFloat(STEP_LEN, stepLength);
    }

    public float getBodyWeight()
    {
        float bodyWeight = prefsManager.getFloat(BODY_WEIGHT);
        if (bodyWeight == 0.0f)
        {
            return 60.0f;
        }
        Log.d(TAG, "getBodyWeight: 获取用户体重：" + bodyWeight);
        return bodyWeight;
    }

    public void setBodyWeight(float bodyWeight)
    {
        prefsManager.putFloat(BODY_WEIGHT, bodyWeight);
    }


}
