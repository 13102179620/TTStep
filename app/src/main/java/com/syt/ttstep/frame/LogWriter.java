package com.syt.ttstep.frame;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogWriter {

    private static final String DEBUG_TAG = "-syt";
    private static boolean isDebug = true;
    //是否将日志输出至文件
    private static boolean isWriteToLog = false;
    private static BufferedWriter bw;


    //封装日志到文件
    public static void LogToFile(String tag , String logText){

        if (!isWriteToLog)
            return;
        String needWriteMsg = tag + " : " + logText;

        String fileName = Environment.getExternalStorageDirectory().getPath()
                +"/LogFile.txt";

        File file = new File(fileName);

        try {
            FileWriter fileWriter = new FileWriter(file ,true);
            bw = new BufferedWriter(fileWriter);
            bw.write(needWriteMsg);
            bw.newLine();
            bw.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bw!=null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void d(String msg){
        if (LogWriter.isDebug)
            Log.d(LogWriter.DEBUG_TAG , msg);

        if (LogWriter.isWriteToLog){
            LogWriter.LogToFile(LogWriter.DEBUG_TAG , msg);
        }
    }


}
