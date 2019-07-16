package com.syt.ttstep.utils;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.content.Context;

import java.util.Iterator;
import java.util.List;

public class ServicesUtils {

    //判断服务是否在运行
    public static boolean isServiceRunning(Context context , String serviceName){

        if (context == null || serviceName == null){
            return false;
        }

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List< ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        Iterator iterator = serviceList.iterator();
        while (iterator.hasNext()){
            ActivityManager.RunningServiceInfo runningServiceInfo =(ActivityManager.RunningServiceInfo) iterator.next();
            if (serviceName.trim().equals(runningServiceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

}
