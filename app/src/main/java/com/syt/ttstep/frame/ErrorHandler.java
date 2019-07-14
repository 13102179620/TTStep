package com.syt.ttstep.frame;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class ErrorHandler implements Thread.UncaughtExceptionHandler {

    //单利模式
    private volatile static ErrorHandler instance;
    private ErrorHandler(){}
    public static ErrorHandler getInstance(){
        if (instance == null){
            synchronized (ErrorHandler.class){
                if (instance == null)
                    instance = new ErrorHandler();
            }
        }
        return instance;
    }


    //设置当前handler设置为error捕获器
    public void setErrorHandler(Context context){
        Thread.setDefaultUncaughtExceptionHandler(this);
    }



    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        //崩溃信息写入文件
        LogWriter.LogToFile("Error" , "崩溃信息：" + throwable.getMessage());
        LogWriter.LogToFile("Error" , "崩溃线程："  + thread.getName() + "线程ID:" + thread.getId());

        StackTraceElement[] trace = throwable.getStackTrace();
        for (StackTraceElement e : trace){
            LogWriter.LogToFile("Error","Lines:" + e.getLineNumber() + " : " + e.getMethodName());
        }
        throwable.printStackTrace();
    }
}
