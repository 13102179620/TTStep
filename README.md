# TTSTep超简单计步APP
## 1.简单介绍
使用AIDL Service 实现远程通信服务。可实时刷新运动步数，并在BarChart上显示
，可自定义计步器设置，APP可根据设置的传感器灵敏度，采样时间，用户的步长体重，动态的进行步数记录以及卡路里消耗计算。
<p align="center">
	<img src="https://github.com/13102179620/TTStep/raw/master/md/ttstep2.png?raw=true" alt="Sample"  width="200" height="384">
	<img src="https://github.com/13102179620/TTStep/raw/master/md/ttstep3.png?raw=true" alt="Sample"  width="200" height="384">
	<img src="https://github.com/13102179620/TTStep/raw/master/md/ttstep4.png?raw=true" alt="Sample"  width="200" height="384">
</p>

## 2.业务逻辑
1. 闪屏页实现
```java
@Override
    protected void initView() {
        btnSkip = findViewById(R.id.btn_skip);
        mHandler = new Handler();
        mRunable = new Runnable() {
            @Override
            public void run() {
                toHomeActivity();
            }
        };
    }
```
这里借助handler的postDelayed(mRunable , 3000)方法实现三秒后跳转。
要注意的是如果点击了跳转按钮，要移除handler的runnable。
```java
private void initEvent() {
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //取消handler的子线程
                mHandler.removeCallbacks(mRunable);
                toHomeActivity();
                finish();
            }
        });
    }
```
2.HomeActivity实现
首先在Activty的oncreateu过程中与AIDL Service绑定，由于并不清楚服务运行状态，所以需要判断以何种方式启动service。绑定成功后，设置启动sercie的计步服务，并启动两个线程，循环的读取service的数据。
```java
@Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 绑定成功！");
            remoteService = IPedometerService.Stub.asInterface(service);
            try {
                status = remoteService.getServiceRunningStatus();
                //如果计步服务在运行,则直接启动线程开始计步
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
```
之后点击启动按钮开始计步，采用在线程中循环给handler发送消息，执行更新service计步数据
```java
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
//                        mHandler.sendEmptyMessageDelayed(MESSAGE_UP_DATE_STEPS_COUNT,GET_STEP_COUNT_POST_TIME);
                        mHandler.sendEmptyMessage(MESSAGE_UP_DATE_STEPS_COUNT);
                        Thread.sleep(200);
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
```
图表数据的更新需要AIDL传递序列化对象，所以实现了一个实体类，实现Parcelable接口，将图标数据传回,更新图表。service每一分钟更新一次，对应图表x轴为横坐标。
```java
chartBean = remoteService.getChartData();
if (chartBean != null)
    Log.d(TAG, "startStepCount: 获取图表数据 " + chartBean.toString());
else
    Log.d(TAG, "startStepCount: chart为null");
updateChartData(chartBean);
```


                                                                                                  


