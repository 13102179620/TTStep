# TTSTep超简单计步APP
## 1.简单介绍
使用AIDL Service 实现远程通信服务。可实时刷新运动步数，并在BarChart上显示
，可自定义计步器设置，APP可根据设置的传感器灵敏度，采样时间，用户的步长体重，动态的进行步数记录以及卡路里消耗计算。
<p align="center">
	<img src="https://github.com/13102179620/TTStep/raw/master/md/ttstep.png?raw=true" alt="Sample"  width="200" height="384">
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
首先在Activty的oncreateu过程中与AIDL Service绑定，由于并不清楚服务运行状态，所以需要判断以何种方式启动service。绑定成功后，设置启动sercie的计步服务，并启动两个线程，循环的读取service的数据。这里注意以NEW_TASK去绑定service
```java
if (!ServicesUtils.isServiceRunning(this, PedometerService.class.getName())) {
            intent = new Intent(this, PedometerService.class);
            startService(intent);
        }

        if (intent == null) {
            intent = new Intent(this.getApplicationContext(),PedometerService.class);
        }
        // 设置新TASK的方式
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 以bindService方法连接绑定服务
        isBandService = bindService(intent, serviceConnection, BIND_AUTO_CREATE);
```
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

3. AIDL Service
AIDL需要注册SensorManager,根据设备版本号，kitkat以下使用加速度传感器，kitakat以上直接使用计步传感器。该Service 提供的功能：
    - 计算卡路里以及行走距离：
		从Setting类中获取用户设置的体重步长信息，与步数做计算，返回给调用的Activity
		```java
		public double getCalorieBySteps(int stepCount){
			 //步长
			 float stepLen = mSettings.getSetpLength() ;
			 //体重
			 float bodyWeight = mSettings.getBodyWeight();

			 double calorie = bodyWeight * 0.8  * stepLen * stepCount/100000.0f;

			 return calorie;
	 }
	 ```
	 而Setting类其实也是调用SharePreference进行设置的保存。
	 ```java
	 public float getBodyWeight()
    {
        float bodyWeight = prefsManager.getFloat(BODY_WEIGHT);
        if (bodyWeight == 0.0f)
        {
            return 60.0f;
        }
        Log.d(TAG, "getBodyWeight: 获取用户体重：" + bodyWeight);
        return bodyWeight;
    }
		```
	- 获取步数：
	创建实体类PedometerBean，保存计步数据等信息，以天为时间戳保存在数据库（每天只有一个该实体，实现状态保存，这部分内容未完成）。并传入计步接口进行加工，传感器每次有效的计步探测都会修改实体的stepcount属性。远程Activity也是通过service调用该实体的getStepCount方法获取计步数

	- 存入数据库：
	保存有用户的很多信息，为后续开发一天为单位的表格详情预留数据与接口
	```java
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
```
- 其他功能见代码，都是通过实体类实现，扩展性强

4. 加速度传感器的计步接口功能实现
考虑要过滤轻微抖动以及大幅度抖动，还要判断方向变化，所以要保存两次加速度变化来判断是否为一次有效的计步：
```java
if (sensor.getType() == Sensor.TYPE_ACCELEROMETER){
		float sum = 0;
		//三向平均变化量
		for (int i = 0; i < 3; i++) {
				float vector = offset + sensorEvent.values[i] * mScale;
				sum += vector;
		}
		float average = sum/3;
		float direction;
		//规定方向（2种）
		if (average > mLastValue) {
				direction = 1;
		}else if(average < mLastValue) {
				direction = -1;
		}else {
				direction = 0;
		}
		//与上次方向相反，才算有效
		if (direction == -mLastDirection){
				//规定01
				int extType = (direction > 0 ? 0 : 1);
				mLastExtrems[extType] = mLastValue;
				//向量变化绝对值 这次-上次  也可以不加abs 因为extType保存了符号信息
				float diff = Math.abs(mLastExtrems[extType] - mLastExtrems[1 - extType]);
				//过滤太微小的向量
				if (diff > sensitivity){
						boolean isLargeEnough = diff > (mLastDiff * 2/3);
						boolean isPreLargeEnough = mLastDiff > (diff/3);
						//方向是否是变化的
						boolean isDifferentDirect  =(mLastMatch != 1 - extType);
						if (isLargeEnough && isPreLargeEnough && isDifferentDirect){
								end = System.currentTimeMillis();
								//如果这次向量变化有效
								if ( end - start > mLimits){
										 stepsCount++;
										 mLastMatch = extType;
										 start = end;
										 mLastDiff = diff;
										 if (data != null){
												 //对传入的databean赋值
												 data.setStepsCount(stepsCount);
												 data.setLastStepTime(System.currentTimeMillis());
										 }
								}else{
										//恢复初始化
										mLastDiff = sensitivity;
								}
						}else {
								mLastMatch = -1;
								mLastDiff = sensitivity;
						}
				}
		}
		mLastDirection = direction;
		mLastValue = average;
}
	 ```

	 计步传感器就很简单了，直接获取返回值就行。
                                            


