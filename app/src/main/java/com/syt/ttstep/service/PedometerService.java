package com.syt.ttstep.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.syt.ttstep.Settings.ACache;
import com.syt.ttstep.Settings.Settings;
import com.syt.ttstep.beans.PedometerBean;
import com.syt.ttstep.beans.PedometerChartBean;
import com.syt.ttstep.db.DBHelper;
import com.syt.ttstep.frame.FrameApplication;
import com.syt.ttstep.utils.DateUtils;
import com.syt.ttstep.utils.JsonUtils;

public class PedometerService extends Service {

    public static final String TAG = "PedometerSercice-app";
    public static final int STATUS_NOT_RUN = 0;
    public static final int STATUS_RUNNING = 1;
    // TODO: 2019/7/15 改回来
    private static final long UPDATE_CHAR_TIME = 5000L;//60秒

    private SensorManager mSensorManager;
    private PedometerBean mPedometerBean;
    private PedometerListenr mPedometerListenr;
    private Settings mSettings;
    private PedometerChartBean mPedometerChartBean;

    private Handler mHandler = new Handler();

    //每隔一分钟，更新一次数据
    private Runnable mTimeRunnable = new Runnable() {
        @Override
        public void run() {
            if (runState == STATUS_RUNNING){
                if (mHandler != null && mPedometerChartBean != null){
                    //防止多次发送，上一个没发完，下一个就发了
                    mHandler.removeCallbacks(mTimeRunnable);
                    updateChartData();
                    mHandler.postDelayed(mTimeRunnable , UPDATE_CHAR_TIME);
                }
            }
        }
    };
    private int runState = STATUS_NOT_RUN;



    public PedometerService() {
    }

    public  double  getStepDistance(){

        float stepLen = mSettings.getSetpLength();
        double distance = (mPedometerBean.getStepsCount() * (long)(stepLen))/100000.0f;
        return distance;
    }

    public double getCalorieBySteps(int stepCount){
        //步长
        float stepLen = mSettings.getSetpLength() ;
        //体重
        float bodyWeight = mSettings.getBodyWeight();

        //定义计算公式：
        //热量 = 体重kg* 距离km * 1.027  or  0.708
        double WALKING_FACTOR = 0.708;
        double RUNNING_FACTOR = 1.027;

        double calorie = (bodyWeight * WALKING_FACTOR) * stepLen * stepCount/100000.0f;

        return calorie;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mPedometerBean = new PedometerBean();
        mPedometerListenr = new PedometerListenr(mPedometerBean);
        mSettings = new Settings(this);
        mPedometerChartBean = new PedometerChartBean();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return iPedometerService;
    }


    //更新chardata数据
    private void updateChartData(){
        if (mPedometerChartBean.getIndex() < PedometerChartBean.MaxIndex - 1){
            //更新x轴索引
            mPedometerChartBean.setIndex(mPedometerChartBean.getIndex() + 1);
            //更新步数
            mPedometerChartBean.getArrays()[mPedometerChartBean.getIndex()] = mPedometerBean.getStepsCount();
        }
    }



    //载入缓存
    private void saveChartBeanData(){
        String json = JsonUtils.objToJson(mPedometerChartBean);
        ACache.get(getApplicationContext()).put("JsonChardata" , json);
    }



    //远程AIDL服务接口
    private IPedometerService.Stub iPedometerService = new IPedometerService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        //获取总步数
        @Override
        public int getStepsCount() throws RemoteException {

            if (mPedometerBean != null){
                Log.d(TAG, "getStepsCount: 已经走了"+ mPedometerBean.getStepsCount() + "步了!");
                return mPedometerBean.getStepsCount();
            }

            return 0;
        }

        //重置
        @Override
        public void resetCount() throws RemoteException {
            if (mPedometerBean != null){
                mPedometerBean.reset();
                saveData();
            }
            //更新缓存中的数据（清楚）
            if(mPedometerChartBean != null){
                mPedometerChartBean.reset();
                saveChartBeanData();
            }


            if (mPedometerListenr != null){
                mPedometerListenr.setStepsCount(0);
            }
        }

        //开始计步
        @Override
        public void startSetpsCount() throws RemoteException {
            if (mSensorManager != null && mPedometerListenr != null){
                Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.registerListener(mPedometerListenr, sensor , SensorManager.SENSOR_DELAY_NORMAL);
                mPedometerBean.setStartTime(System.currentTimeMillis());
                mPedometerBean.setDay(DateUtils.getTimestempByDay());
                runState = STATUS_RUNNING;
                Log.d(TAG + "startStepCountService", "startSetpsCount: 计步器服务开始");
                //handler 开始发送消息
                mHandler.postDelayed(mTimeRunnable , UPDATE_CHAR_TIME);


            }
        }

        @Override
        public void stopSetpsCount() throws RemoteException {
            if (mSensorManager != null && mPedometerListenr != null){

                Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mSensorManager.unregisterListener(mPedometerListenr, sensor);
                runState = STATUS_NOT_RUN;
                Log.d(TAG, "stopSetpsCount: 计步器服务停止");
                //handler停止发送
                mHandler.removeCallbacks(mTimeRunnable);
            }

        }

        @Override
        public double getCalorie() throws RemoteException {
            if (mPedometerBean != null ){
                return getCalorieBySteps(mPedometerBean.getStepsCount());
            }

            return 0;
        }

        @Override
        public double getDistance() throws RemoteException {
            if (mPedometerBean != null){
                // TODO: 2019/7/14
                return getStepDistance();
            }
            return 0;
        }

        @Override
        public void saveData() throws RemoteException {
            if ( mPedometerBean != null){
                //io读写
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        DBHelper dbHelper = new DBHelper(PedometerService.this , DBHelper.PedometerDbName );
                        try {
                            mPedometerBean.setDistance(getDistance());
                            mPedometerBean.setCalorie(getCalorieBySteps(mPedometerBean.getStepsCount()));
                            //开始计步到结束计步的时间（s）
                            long time = (mPedometerBean.getLastStepTime() - mPedometerBean.getStartTime())/1000;
                            if (time == 0 ){
                                //设置多少步/min
                                mPedometerBean.setPace(0);
                                mPedometerBean.setSpeed(0);
                            }else {
                                int pace = Math.round(60 ^ mPedometerBean.getStepsCount()/time);
                                mPedometerBean.setPace(pace);
                                //单位：km/h
                                long speed = Math.round((mPedometerBean.getDistance()/1000)/(time/3600));
                            }
                            dbHelper.write2Database(mPedometerBean);
                            Log.d(TAG, "run: 保存至数据库:" + mPedometerBean.toString() );
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

        @Override
        public void setSensitivity(float sensitivity) throws RemoteException {
            if (mSettings != null){
                mSettings.setSensitivity((float)sensitivity);
                Log.d(TAG, "setSensitivity: 设置灵敏度为：" + sensitivity);
            }
            if (mPedometerListenr != null){
                mPedometerListenr.setSensitivity(sensitivity);
            }
        }

        @Override
        public double getSensitivity() throws RemoteException {
            if (mSettings != null){
                return mSettings.getSensitivity();
            }
            return 10.0f;
        }

        @Override
        public int getInterval() throws RemoteException {
            if (mSettings != null){
                return mSettings.getInterval();
            }
            return 300;
        }

        @Override
        public void setInterval(int interval) throws RemoteException {
            if (mSettings != null){
                mSettings.setInterval(interval);
            }
            if (mPedometerListenr != null){
                mPedometerListenr.setmLimits(interval);
            }
        }

        @Override
        public long getStartTimestmp() throws RemoteException {

            if (mPedometerBean != null){
                Log.d(TAG, "getStartTimestmp: 获取开始时间戳：" + mPedometerBean.getStartTime());
                return  mPedometerBean.getStartTime();
            }
            return 0L;
        }

        @Override
        public int getServiceRunningStatus() throws RemoteException {
            Log.d(TAG, "getServiceRunningStatus: 获得运行信息" + runState);
            return runState;
        }

        @Override
        public PedometerChartBean getChartData() throws RemoteException {
            return mPedometerChartBean;
        }
    };



}
