package com.syt.ttstep.frame;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

public class FrameApplication extends Application {

    private static LinkedList<Activity> actList = new LinkedList<>();
    //PreferenceShare 封装类
    private PrefsManager prefsManager;
    private static FrameApplication instance;
    //错误日志封装类
    private ErrorHandler errorHandler;

    private FrameApplication(){}

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        prefsManager = new PrefsManager(this);
        errorHandler = ErrorHandler.getInstance();
    }

    public PrefsManager getPrefsManager() {
        return prefsManager;
    }

    //获取单例
    public static FrameApplication getInstance(){
        return instance;
    }

    //获取所有Activity列表
    public LinkedList<Activity> getActList(){
        return actList;
    }



    //添加activity
    public static void addToActivityList(Activity act){

        if (act != null){
            actList.add(act);
        }
    }
    //删除activity
    public static void removeFromActivityList(Activity act){
        if (actList!=null && actList.size() > 0 && actList.indexOf(act) != -1){
            actList.remove(act);
        }
    }
    //清空activity列表
    public static void clearActList(){
        for (int i = actList.size() - 1 ; i>=0 ; i--){
            Activity activity = actList.get(i);
            activity.finish();
        }
    }

    //完全退出app
    public static void exitApp(){

        try{
            clearActList();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }



}
