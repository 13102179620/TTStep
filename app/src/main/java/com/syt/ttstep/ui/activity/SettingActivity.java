package com.syt.ttstep.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.syt.ttstep.R;
import com.syt.ttstep.Settings.Settings;
import com.syt.ttstep.frame.BaseActivity;
import com.syt.ttstep.frame.LogWriter;
import com.syt.ttstep.service.IPedometerService;
import com.syt.ttstep.service.PedometerService;
import com.syt.ttstep.utils.ServicesUtils;
import com.syt.ttstep.utils.StringUtils;

import static com.syt.ttstep.service.PedometerService.STATUS_RUNNING;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "settingActivity-app";

    private ListView settingListVIew;
    private View toolBar;
    private TextView tvTitle;
    private ImageView ivBack;
    private ImageView ivSetting;
    private SettingListAdapter adapter;
    private IPedometerService remoteService;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        initEvent();
    }

    private void initEvent() {
        //点击箭头退出设置
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: SettingFinish!!!!!!");
                finish();
            }
        });
    }

    @Override
    protected void initView() {
        settingListVIew = findViewById(R.id.listView);
        toolBar = findViewById(R.id.setting_toolbar);
        tvTitle = toolBar.findViewById(R.id.tv_title);
        tvTitle.setText("设 置");
        ivBack = toolBar.findViewById(R.id.iv_back);
        ivSetting = toolBar.findViewById(R.id.iv_setting);
        ivSetting.setVisibility(View.GONE);
        adapter = new SettingListAdapter();
        settingListVIew.setAdapter(adapter);

    }

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
        Boolean isBindSercive = bindService(intent , serviceConnection , BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: 绑定成功！");
            remoteService = IPedometerService.Stub.asInterface(service);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteService = null;
        }
    };





    public class SettingListAdapter extends BaseAdapter {

        private Settings settings = null;
        private String[] listTitle = {"设置步长", "设置体重", "传感器灵敏度", "传感器采样时间"};


        public SettingListAdapter(){
            settings = new Settings(SettingActivity.this);
        }






        @Override
        public int getCount() {
            return listTitle.length;
        }

        @Override
        public Object getItem(int position) {
            if (position < listTitle.length)
                return listTitle[position];
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder = null;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(SettingActivity.this);
                convertView = inflater.inflate(R.layout.item_setting, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.tvTitle = convertView.findViewById(R.id.title);
                viewHolder.tvDesc = convertView.findViewById(R.id.desc);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.tvTitle.setText(listTitle[position]);

            switch (position) {
                case 0:
                    final float stepLen = settings.getSetpLength();
                    viewHolder.tvDesc.setText(String.format("计算距离和消耗的热量：%s CM" , StringUtils.getFormatSring(stepLen)));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            stepClick(stepLen);
                        }
                    });
                    break;
                case 1:

                    final float bodyWeight = settings.getBodyWeight();
                    viewHolder.tvDesc.setText(String.format("通过体重计算消耗的热零：%s KG" ,StringUtils.getFormatSring(bodyWeight)));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            weightClick(bodyWeight);
                        }
                    });

                    break;
                case 2:
                    double sensitivity = settings.getSensitivity();
                    viewHolder.tvDesc.setText(String.format("设置传感器灵敏度：%s " , StringUtils.getFormatSring(sensitivity)));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sensitiveClick();
                        }
                    });
                    break;
                case 3:

                    int InterVals = settings.getInterval();
                    viewHolder.tvDesc.setText(String.format("每隔：%s 秒采集一次数据 " ,StringUtils.getFormatSring(InterVals)));
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            IntervalClick();
                        }
                    });

                    break;
                default:

            }

            return convertView;
        }

        private void stepClick(float stepLen) {
            AlertDialog.Builder builder= new AlertDialog.Builder(SettingActivity.this);
            builder.setTitle("设置步长");
            View view = View.inflate(SettingActivity.this , R.layout.setting_dialog_input , null);
            final EditText editText = view.findViewById(R.id.input);
            editText.setText(String.valueOf(stepLen));
            builder.setView(view);
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String val = editText.getText().toString();
                    if (val != null && val.length() > 0){
                        float len = Float.parseFloat(val);
                        settings.setStepLength(len);
                        if (adapter != null){
                            adapter.notifyDataSetChanged();
                        }
                    }else
                        Toast.makeText(SettingActivity.this , "请输入正确的参数！" , Toast.LENGTH_LONG);
                }
            });
            builder.create().show();
        }

        private void weightClick(float pBodyWeight) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setTitle("设置体重");
            View mView = View.inflate(SettingActivity.this, R.layout.setting_dialog_input, null);
            final EditText mInput = (EditText) mView.findViewById(R.id.input);
            mInput.setText(String.valueOf(pBodyWeight));
            mBuilder.setView(mView);
            mBuilder.setNegativeButton("取消", null);
            mBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    String val = mInput.getText().toString();
                    if (val != null && val.length() > 0)
                    {
                        float bodyWeight = Float.parseFloat(val);
                        settings.setBodyWeight(bodyWeight);
                        if (adapter != null)
                        {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    else
                    {
                        Toast.makeText(SettingActivity.this, "请输入正确的参数!" , Toast.LENGTH_SHORT);
                    }
                }
            });
            mBuilder.create().show();
        }

        private void sensitiveClick() {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this);
            mBuilder.setItems(R.array.sensitive_array, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    //调用服务设置灵敏度
                    if (remoteService != null){
                        try {
                            remoteService.setSensitivity(Settings.SENSITIVE_ARRAY[which]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    settings.setSensitivity(Settings.SENSITIVE_ARRAY[which]);
                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mBuilder.setTitle("设置传感器灵敏度");
            mBuilder.create().show();
        }

        private void IntervalClick() {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(SettingActivity.this, AlertDialog.THEME_HOLO_LIGHT);
            mBuilder.setItems(R.array.interval_array, new DialogInterface.OnClickListener()
            {
                public void onClick(DialogInterface dialog, int which)
                {
                    //调用服务设置采样时间间隔
                    if (remoteService != null){
                        try {
                            remoteService.setInterval(Settings.INTERVAL_ARRAY[which]);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    settings.setInterval(Settings.INTERVAL_ARRAY[which]);
                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            mBuilder.setTitle("设置传感器采样间隔");
            mBuilder.create().show();
        }


    }


    private class ViewHolder {
        TextView tvTitle;
        TextView tvDesc;
    }

}
