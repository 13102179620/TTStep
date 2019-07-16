package com.syt.ttstep.ui.activity;


import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.syt.ttstep.R;
import com.syt.ttstep.Settings.Settings;
import com.syt.ttstep.beans.PedometerChartBean;
import com.syt.ttstep.frame.BaseActivity;
import com.syt.ttstep.frame.LogWriter;
import com.syt.ttstep.frame.PrefsManager;
import com.syt.ttstep.service.IPedometerService;
import com.syt.ttstep.service.PedometerService;
import com.syt.ttstep.utils.MathCaculateUtils;
import com.syt.ttstep.utils.ServicesUtils;
import com.syt.ttstep.widget.CircleProgressBar;

import java.util.ArrayList;

import static android.content.Context.BIND_AUTO_CREATE;

public class HomeActivity extends BaseActivity {

    public static final String TAG = "HomeActivity-app";
    private CircleProgressBar progressBar;
    private TextView tvCalorie;
    private TextView tvTime;
    private TextView tvDistance;
    private TextView tvStepsCount;
    private Button btnReset;
    private Button btnStart;
    private BarChart barChart;
    private PedometerChartBean chartBean;
    private View toolBar;
    private ImageView ivSetting;
    private ImageView ivback;
    private MyHandler mHandler;

    private IPedometerService remoteService;
    private PrefsManager prefsManager = null;
    //保存当前服务状态
    private int status = -1;
    private static final int STATUS_NOT_RUNNING = 0;
    private static final int STATUS_RUNNING = 1;
    private boolean isChartUpdate = false;
    private boolean isRunning = true;
    private boolean isBandService;


    private static final int MESSAGE_UP_DATE_STEPS_COUNT = 1000;
    private static final int MESSAGE_UP_DATE_CHART_DATA = 1001;

    //默认每200ms获取一次计步服务中的数据
    private static  int getStepCountPostTime = 5000;
    //默认每60s获取一次chart更新数据
    private static final int GET_CHART_DATA_UPDATE_POST_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initEvent();
        requestData();
    }

    //设置完成后更新采样时间
    @Override
    protected void onRestart() {
        super.onRestart();
        prefsManager = new PrefsManager(this);
        getStepCountPostTime = prefsManager.getInt(Settings.INTERVAL);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    //oncreate中先条用该方法,进行服务绑定，但是并没有启动计步功能
    @Override
    protected void requestData() {

        Intent intent = null;

        //如果服务没有再运行则显示的startService
        if (!ServicesUtils.isServiceRunning(this, PedometerService.class.getName())) {
            intent = new Intent(this, PedometerService.class);
            startService(intent);
            Log.d(TAG, "requestData: 以start方式启动计步服务");
        }

        if (intent == null) {
            Log.d(TAG, "requestData: 计步服务已经运行，正在启动！");
            intent = new Intent(this.getApplicationContext(), PedometerService.class);

        }
        // 设置新TASK的方式
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 以bindService方法连接绑定服务

        isBandService = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        Log.d(TAG, "requestData: 正在绑定服务！" + isBandService);


        if (isBandService && remoteService != null) {

            try {
                status = remoteService.getServiceRunningStatus();
                Log.d(TAG, "requestData: status:" + status);
                if (status == PedometerService.STATUS_NOT_RUN) {
                    btnStart.setText("启动");
                } else if (status == PedometerService.STATUS_RUNNING) {
                    btnStart.setText("停止");
                    isRunning = true;
                    isChartUpdate = true;
                    new Thread(new StepCountUpdateRunnable()).start();
                    new Thread(new ChartUpdateRunnable()).start();
                }
            } catch (RemoteException e) {
                Log.d(TAG, "requestData: 捕获异常" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if (remoteService == null) {
                Log.d(TAG, "requestData: remoteService == null");
            }
            btnStart.setText("启动");
        }
    }


    //绑定远程计步服务的回调接口
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 绑定成功！");
            remoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = remoteService.getServiceRunningStatus();
                //如果计步服务在运行
                if (status == STATUS_RUNNING) {
                    startStepCount();
                } else {
                    btnStart.setText("启动");
                }
            } catch (RemoteException e) {
                e.printStackTrace();
                LogWriter.d(e.toString());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            btnStart.setText("启动");
            isRunning = false;
            isChartUpdate = false;
            Log.d(TAG, "onServiceDisconnected: 取消绑定");
        }
    };


    //启用计步功能，新建两个线程分别循环更新远程服务中的数据
    private void startStepCount() throws RemoteException {

        btnStart.setText("停止计步");
        isChartUpdate = true;
        isRunning = true;
        //启动线程更新数据
        new Thread(new StepCountUpdateRunnable()).start();
        new Thread(new ChartUpdateRunnable()).start();

        chartBean = remoteService.getChartData();
        if (chartBean != null)
            Log.d(TAG, "startStepCount: 获取图表数据 " + chartBean.toString());
        else
            Log.d(TAG, "startStepCount: chart为null");
        updateChartData(chartBean);
    }


    //自定义线程获取计步service中的服务状态，发送更新计步数消息给handler更新ui
    private class StepCountUpdateRunnable implements Runnable {

        @Override
        public void run() {
            while (isRunning) {
                try {
                    status = remoteService.getServiceRunningStatus();
                    Log.d(TAG, "run: 按下按钮后 ， status为：" + status);
                    if (status == STATUS_RUNNING) {
                        //防止上次msg未处理在队列中阻塞
                        mHandler.removeMessages(MESSAGE_UP_DATE_STEPS_COUNT);
                        //发送消息更新数据
//                        mHandler.sendEmptyMessageDelayed(MESSAGE_UP_DATE_STEPS_COUNT,getStepCountPostTime);
                        mHandler.sendEmptyMessage(MESSAGE_UP_DATE_STEPS_COUNT);

                        Thread.sleep(getStepCountPostTime);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                    LogWriter.d(e.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogWriter.d(e.toString());
                }

            }
        }
    }


    //自定义线程获取计步service中的服务状态，发送更新chartbar图表数据消息给handler
    private class ChartUpdateRunnable implements Runnable {

        @Override
        public void run() {
            while (isChartUpdate) {
                try {
                    mHandler.removeMessages(MESSAGE_UP_DATE_CHART_DATA);
                    chartBean = remoteService.getChartData();
                    mHandler.sendEmptyMessage(MESSAGE_UP_DATE_CHART_DATA);
                    Thread.sleep(GET_CHART_DATA_UPDATE_POST_TIME);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    LogWriter.d(e.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    LogWriter.d(e.toString());

                }
            }
        }
    }


    //静态内部类handler防止leak,接受自线程发送的消息，丛Service更新数据
    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MESSAGE_UP_DATE_CHART_DATA:
                    mHandler.removeMessages(MESSAGE_UP_DATE_CHART_DATA);
                    if (chartBean != null) {
                        updateChartData(chartBean);
                    }
                    break;


                case MESSAGE_UP_DATE_STEPS_COUNT:
                    //更新计步数据
                    mHandler.removeMessages(MESSAGE_UP_DATE_STEPS_COUNT);
                    updateStepCount();
                    break;

                default:
                    LogWriter.d("default: " + msg.what);
            }

            super.handleMessage(msg);
        }

    }


    //更新计步数
    public void updateStepCount() {
        if (remoteService != null) {
            int stepCountVal = 0;
            double calorieVal = 0.0d;
            double distanceVal = 0.0d;

            try {
                stepCountVal = remoteService.getStepsCount();
                calorieVal = remoteService.getCalorie();
                distanceVal = remoteService.getDistance();
                Log.d(TAG, "updateStepCount: 更新计步数成功！" + stepCountVal + " " + calorieVal + " " + distanceVal);

            } catch (RemoteException e) {
                e.printStackTrace();
                Log.d(TAG, "updateStepCount: 捕获异常");
                LogWriter.d(e.toString());
            }
            //更新数据到ui
            tvStepsCount.setText(String.valueOf(stepCountVal + "步"));
            tvCalorie.setText(MathCaculateUtils.getFormatVal(calorieVal) + "大卡");
            tvDistance.setText(MathCaculateUtils.getFormatVal(distanceVal));
            progressBar.setProgress(stepCountVal);

        }
    }

    //更新chartbar数据
    private void updateChartData(PedometerChartBean bean) {
        // TODO: 2019/7/13 横坐标
        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
        if (bean != null) {
            for (int i = 0; i < bean.getIndex(); i++) {
                xVals.add(String.valueOf(i) + "分s");
                int valY = bean.getArrays()[i];
                yVals.add(new BarEntry(i, valY));
            }
            tvTime.setText(String.valueOf(bean.getIndex()) + "分");
            BarDataSet set = new BarDataSet(yVals, "所走的步数");
            set.setBarBorderWidth(1.2f);

            BarData data = new BarData(set);
            data.setValueTextSize(10f);

            Description description = new Description();
            description.setText("您走的步数");
            barChart.setDescription(description);
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setAxisMinimum(0f);
            YAxis rightAxis = barChart.getAxisRight();
            ValueFormatter formatter = new IndexAxisValueFormatter(xVals);
            xAxis.setValueFormatter(formatter);
            barChart.setData(data);
            barChart.invalidate();
        }

    }


    //自定义点击事件处理类
    private class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.btn_reset:
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(HomeActivity.this);
                    mBuilder.setTitle("确认重置");
                    mBuilder.setMessage("您的记录将会被清零,确定吗?");
                    mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (remoteService != null) {
                                try {
                                    remoteService.stopSetpsCount();
                                    remoteService.resetCount();
                                    //更新清除后的表格数据
                                    chartBean = remoteService.getChartData();
                                    updateChartData(chartBean);
                                    status = remoteService.getServiceRunningStatus();
                                    if (status == STATUS_RUNNING) {
                                        btnStart.setText("停止");
                                    } else if (status == STATUS_NOT_RUNNING) {
                                        btnStart.setText("启动");
                                    }
                                } catch (RemoteException e) {
                                    LogWriter.d(e.toString());
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                    mBuilder.setNegativeButton("取消", null);
                    AlertDialog dlg = mBuilder.create();
                    dlg.show();
                    break;

                case R.id.btn_Start:
                    try {
                        status = remoteService.getServiceRunningStatus();
                        Log.d(TAG, "onClick: status:" + status);

                    } catch (RemoteException e) {
                        Log.e(TAG, "onClick: 捕获异常" + e.getMessage());
                        e.printStackTrace();
                    }
                    //如果当前服务正在运行，则将远程服务停止，并且关闭两个子线程
                    if (status == STATUS_RUNNING && remoteService != null) {
                        try {
                            Log.d(TAG, "onClick: 点击结束，正在结束计步服务");
                            remoteService.stopSetpsCount();
                            btnStart.setText("启动");
                            isRunning = false;
                            isChartUpdate = false;
                        } catch (RemoteException e) {
                            LogWriter.d(e.getMessage() + " 捕获异常");
                            e.printStackTrace();
                        }

                        //如果服务未启动则启动服务，开启线程
                    } else if (status == STATUS_NOT_RUNNING && remoteService != null) {
                        try {
                            Log.d(TAG, "onClick: 点击开始，正在启动计步服务");
                            remoteService.startSetpsCount();
                            startStepCount();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onClick: 捕获异常！");
                        }
                    }
                    break;

                //点击设置跳转到设置界面
                case R.id.iv_setting:
                    Intent intent = new Intent(HomeActivity.this , SettingActivity.class);
                    startActivity(intent);

                case R.id.iv_back:
                    finish();
            }

        }
    }

    //解绑服务
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_UP_DATE_CHART_DATA);
            mHandler.removeMessages(MESSAGE_UP_DATE_STEPS_COUNT);
        }

        if (isBandService) {
            isBandService = false;
            isRunning = false;
            isChartUpdate = false;
            //解绑
            unbindService(serviceConnection);
        }

    }

    private void initEvent() {
        btnStart.setOnClickListener(new MyClickListener());
        btnReset.setOnClickListener(new MyClickListener());
        ivSetting.setOnClickListener(new MyClickListener());
        ivback.setOnClickListener(new MyClickListener());
    }


    protected void initView() {
        progressBar = findViewById(R.id.pb_circleProgressBar);
        progressBar.setProgress(5000);
        progressBar.setMaxProgress(10000);
        barChart = findViewById(R.id.chart1);
        tvCalorie = findViewById(R.id.tv_Calorie);
        tvDistance = findViewById(R.id.tv_distance);
        tvStepsCount = findViewById(R.id.tv_stepCount);
        tvTime = findViewById(R.id.tv_time);
        btnReset = findViewById(R.id.btn_reset);
        btnStart = findViewById(R.id.btn_Start);
        mHandler = new MyHandler();
        toolBar = findViewById(R.id.home_toolbar);
        ivSetting = toolBar.findViewById(R.id.iv_setting);
        ivback = toolBar.findViewById(R.id.iv_back);

    }


    // TODO: 2019/7/15 退出状态保存
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        try {
            remoteService.saveData();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK){
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
                return true;
            }
        }catch (Exception e){

        }

        return super.onKeyDown(keyCode, event);
    }
}
