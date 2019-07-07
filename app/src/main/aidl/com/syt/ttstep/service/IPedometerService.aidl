// IPedometerService.aidl
package com.syt.ttstep.service;

// Declare any non-default types here with import statements

interface IPedometerService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

                void startCount();
                void stopCount();
                void resetCount();

                int getStepsCount();

                double getCalorie();

                double getDistance();

                void saveData();

                void setSensitivity(double sensitivity);


                //传感器灵敏度
                double getSensitivity();

                void setInterval(int interval);

                //获取采样间隔
                int getInterval();

                //读取开始计步时间戳
                long getStartTimeStamp();

                //服务运行状态
                int getServieStatus();
}
