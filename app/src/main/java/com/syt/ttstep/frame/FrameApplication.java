package com.syt.ttstep.frame;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;

public class FrameApplication extends Application {


    private PrefsManager prefsManager;


    public FrameApplication(){}

    @Override
    public void onCreate() {
        super.onCreate();
        prefsManager = new PrefsManager(this);
    }

    public PrefsManager getPrefsManager() {
        return prefsManager;
    }












}
