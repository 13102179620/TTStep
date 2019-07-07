package com.syt.ttstep.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class PedometerService extends Service {
    public PedometerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
