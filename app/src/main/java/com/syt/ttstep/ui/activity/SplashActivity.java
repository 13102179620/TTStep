package com.syt.ttstep.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.syt.ttstep.R;
import com.syt.ttstep.frame.BaseActivity;

public class SplashActivity extends BaseActivity {

    private Handler mHandler;
    private Button btnSkip;
    Runnable mRunable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splah);
        initView();
        initEvent();
        //三秒后跳转至homeactivity
        mHandler.postDelayed(mRunable , 3000);
    }

    //点击跳过按钮马上进入homeactivity
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

    @Override
    protected void requestData() {

    }

    private void toHomeActivity(){
        Intent intent = new Intent(this , HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        //销毁handler ， 防止leak
        mHandler.removeCallbacks(mRunable);
        super.onDestroy();
    }
}
